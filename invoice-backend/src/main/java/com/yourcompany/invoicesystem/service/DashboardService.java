package com.yourcompany.invoicesystem.service;

import com.yourcompany.invoicesystem.dto.DashboardStatsResponse;
import com.yourcompany.invoicesystem.repository.CustomerRepository;
import com.yourcompany.invoicesystem.repository.InvoiceRepository;
import com.yourcompany.invoicesystem.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final InvoiceRepository invoiceRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;

    private static final int LOW_STOCK_THRESHOLD = 10;

    public DashboardStatsResponse getStats() {
        BigDecimal totalSales = invoiceRepository.sumTotalAmount();
        Long totalInvoices = invoiceRepository.count();
        Long totalCustomers = customerRepository.count();
        Long totalProducts = productRepository.count();
        Long lowStockCount = productRepository.countByStockQuantityLessThan(LOW_STOCK_THRESHOLD);

        return new DashboardStatsResponse(
                totalSales != null ? totalSales : BigDecimal.ZERO,
                totalInvoices,
                totalCustomers,
                totalProducts,
                lowStockCount);
    }
}