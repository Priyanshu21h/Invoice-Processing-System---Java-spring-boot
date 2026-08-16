package com.yourcompany.invoicesystem.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

/**
 * InvoiceItemRequest — one line item within an invoice creation request.
 * The client specifies which product and how many units.
 */
@Data
public class InvoiceItemRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be at least 1")
    private Integer quantity;
}
