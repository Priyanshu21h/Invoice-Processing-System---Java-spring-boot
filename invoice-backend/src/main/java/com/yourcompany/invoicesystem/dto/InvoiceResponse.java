package com.yourcompany.invoicesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * InvoiceResponse — outbound DTO containing the full invoice details.
 *
 * It flattens some relationships (like customerName instead of a full Customer object)
 * to keep the API payload clean and focused on what the client needs.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceResponse {
    private Long id;
    private String invoiceNumber;
    private String customerName;
    private LocalDateTime invoiceDate;
    
    private BigDecimal discountPercent;
    private BigDecimal gstAmount;
    private BigDecimal totalAmount;
    
    private List<InvoiceItemResponse> items;
}
