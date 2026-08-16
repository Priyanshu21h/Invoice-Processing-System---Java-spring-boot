package com.yourcompany.invoicesystem.service;

import com.yourcompany.invoicesystem.dto.InvoiceItemRequest;
import com.yourcompany.invoicesystem.dto.InvoiceItemResponse;
import com.yourcompany.invoicesystem.dto.InvoiceRequest;
import com.yourcompany.invoicesystem.dto.InvoiceResponse;
import com.yourcompany.invoicesystem.entity.Customer;
import com.yourcompany.invoicesystem.entity.Invoice;
import com.yourcompany.invoicesystem.entity.InvoiceItem;
import com.yourcompany.invoicesystem.entity.Product;
import com.yourcompany.invoicesystem.exception.InsufficientStockException;
import com.yourcompany.invoicesystem.exception.ResourceNotFoundException;
import com.yourcompany.invoicesystem.repository.CustomerRepository;
import com.yourcompany.invoicesystem.repository.InvoiceRepository;
import com.yourcompany.invoicesystem.repository.ProductRepository;
import com.yourcompany.invoicesystem.util.GstCalculator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * InvoiceService — Core business logic for invoice generation.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  @Transactional is critical here. It ensures atomicity. If anything fails
 * │  (like a stock check throwing an exception halfway through processing),
 * │  the entire transaction rolls back. No partial invoices are saved, and
 * │  no stock is incorrectly decremented.
 * │
 * │  Validate-All-First pattern:
 * │  We first iterate over all items to verify stock availability. ONLY IF all
 * │  items pass the check do we proceed to decrement stock. If we decremented
 * │  while iterating and the last item failed, we'd have to rely on the
 * │  rollback to fix the stock, which works but is less explicit and harder
 * │  to debug. Better to check all, then act.
 * └────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    @Transactional
    public InvoiceResponse createInvoice(InvoiceRequest request) {
        // 1. Fetch Customer
        Customer customer = customerRepository.findById(request.getCustomerId())
                .orElseThrow(() -> ResourceNotFoundException.of("Customer", "id", request.getCustomerId()));

        LocalDateTime now = LocalDateTime.now();

        // 2. Fetch all products and validate stock BEFORE decrementing anything
        // We need to keep track of the fetched products to avoid re-querying the DB.
        List<Product> productsToUpdate = new ArrayList<>();
        for (InvoiceItemRequest itemRequest : request.getItems()) {
            Product product = productRepository.findById(itemRequest.getProductId())
                    .orElseThrow(() -> ResourceNotFoundException.of("Product", "id", itemRequest.getProductId()));

            if (product.getStockQuantity() < itemRequest.getQuantity()) {
                throw new InsufficientStockException(
                        product.getName(), itemRequest.getQuantity(), product.getStockQuantity());
            }
            // Store it so we can update it in the next step
            productsToUpdate.add(product);
        }

        // 3. All items passed validation. Now decrement stock and calculate totals.
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalGst = BigDecimal.ZERO;
        List<InvoiceItem> invoiceItems = new ArrayList<>();

        for (int i = 0; i < request.getItems().size(); i++) {
            InvoiceItemRequest itemRequest = request.getItems().get(i);
            Product product = productsToUpdate.get(i);

            // Decrement stock
            product.setStockQuantity(product.getStockQuantity() - itemRequest.getQuantity());
            productRepository.save(product); // relies on existing locking mechanisms if added later

            // Calculations
            BigDecimal currentPrice = GstCalculator.standardize(product.getPrice());
            int qty = itemRequest.getQuantity();

            BigDecimal lineSubtotal = currentPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal lineGst = GstCalculator.calculateLineGst(currentPrice, qty, product.getGstPercent());

            subtotal = subtotal.add(lineSubtotal);
            totalGst = totalGst.add(lineGst);

            // Build InvoiceItem (Snapshot price at sale!)
            InvoiceItem invoiceItem = InvoiceItem.builder()
                    .product(product)
                    .quantity(qty)
                    .priceAtSale(currentPrice)
                    // invoice reference will be set after Invoice is created
                    .build();
            
            invoiceItems.add(invoiceItem);
        }

        // 4. Apply invoice-level discount
        BigDecimal discountPercent = GstCalculator.standardize(request.getDiscountPercent());
        BigDecimal discountAmount = GstCalculator.calculateDiscount(subtotal, discountPercent);
        
        BigDecimal discountedSubtotal = subtotal.subtract(discountAmount);
        
        // 5. Final totals
        BigDecimal finalTotal = discountedSubtotal.add(totalGst);

        // 6. Generate Invoice Number: INV-YYYYMMDD-XXXX
        String invoiceNumber = generateInvoiceNumber(now);

        // 7. Build and save Invoice
        Invoice invoice = Invoice.builder()
                .invoiceNumber(invoiceNumber)
                .customer(customer)
                .invoiceDate(now)
                .discountPercent(discountPercent)
                .gstAmount(GstCalculator.standardize(totalGst))
                .totalAmount(GstCalculator.standardize(finalTotal))
                .build();

        // Link items to invoice
        for (InvoiceItem item : invoiceItems) {
            item.setInvoice(invoice);
        }
        invoice.setItems(invoiceItems);

        // Save (cascades to items)
        Invoice savedInvoice = invoiceRepository.save(invoice);

        return mapToResponse(savedInvoice);
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getById(Long id) {
        Invoice invoice = invoiceRepository.findById(id)
                .orElseThrow(() -> ResourceNotFoundException.of("Invoice", "id", id));
        return mapToResponse(invoice);
    }

    @Transactional(readOnly = true)
    public Page<InvoiceResponse> getAll(Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        Page<Invoice> invoices;
        
        if (customerId != null && start != null && end != null) {
            invoices = invoiceRepository.findByCustomerIdAndInvoiceDateBetween(customerId, start, end, pageable);
        } else if (customerId != null) {
            invoices = invoiceRepository.findByCustomerId(customerId, pageable);
        } else if (start != null && end != null) {
            invoices = invoiceRepository.findByInvoiceDateBetween(start, end, pageable);
        } else {
            invoices = invoiceRepository.findAll(pageable);
        }
        
        return invoices.map(this::mapToResponse);
    }

    /**
     * Helper to generate a sequential invoice number for the day.
     * Format: INV-YYYYMMDD-XXXX
     */
    private String generateInvoiceNumber(LocalDateTime now) {
        LocalDate today = now.toLocalDate();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.atTime(23, 59, 59, 999999999);
        
        long countToday = invoiceRepository.countByInvoiceDateBetween(startOfDay, endOfDay);
        String dateStr = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        return String.format("INV-%s-%04d", dateStr, countToday + 1);
    }

    /**
     * Map Entity to Response DTO
     */
    private InvoiceResponse mapToResponse(Invoice invoice) {
        List<InvoiceItemResponse> itemResponses = invoice.getItems().stream()
                .map(item -> {
                    BigDecimal lineTotal = item.getPriceAtSale().multiply(BigDecimal.valueOf(item.getQuantity()));
                    return InvoiceItemResponse.builder()
                            .productId(item.getProduct().getId())
                            .productName(item.getProduct().getName())
                            .quantity(item.getQuantity())
                            .priceAtSale(item.getPriceAtSale())
                            .lineTotal(GstCalculator.standardize(lineTotal))
                            .build();
                })
                .toList();

        return InvoiceResponse.builder()
                .id(invoice.getId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .customerName(invoice.getCustomer().getName())
                .invoiceDate(invoice.getInvoiceDate())
                .discountPercent(invoice.getDiscountPercent())
                .gstAmount(invoice.getGstAmount())
                .totalAmount(invoice.getTotalAmount())
                .items(itemResponses)
                .build();
    }
}
