package com.yourcompany.invoicesystem.repository;

import com.yourcompany.invoicesystem.entity.Invoice;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long>,
        JpaSpecificationExecutor<Invoice> {

    /** Filter invoices whose invoiceDate falls between two timestamps. */
    Page<Invoice> findByInvoiceDateBetween(LocalDateTime start, LocalDateTime end, Pageable pageable);

    /** Filter invoices by customer ID with pagination. */
    Page<Invoice> findByCustomerId(Long customerId, Pageable pageable);

    /** Filter by customer AND date range combined. */
    Page<Invoice> findByCustomerIdAndInvoiceDateBetween(
            Long customerId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    /**
     * Count invoices for invoice number generation (to create sequential numbers
     * per day).
     */
    long countByInvoiceDateBetween(LocalDateTime startOfDay, LocalDateTime endOfDay);

    /** Sum of all invoice totals, used for dashboard stats. */
    @Query("SELECT SUM(i.totalAmount) FROM Invoice i")
    BigDecimal sumTotalAmount();
}