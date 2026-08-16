package com.yourcompany.invoicesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * InvoiceItemResponse — one line item in the invoice response.
 * Shows the product snapshot at time of sale (priceAtSale, not current product price).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceItemResponse {
    private Long productId;
    private String productName;
    private Integer quantity;
    private BigDecimal priceAtSale;
    /** lineTotal = priceAtSale × quantity (pre-GST, pre-discount) */
    private BigDecimal lineTotal;
}
