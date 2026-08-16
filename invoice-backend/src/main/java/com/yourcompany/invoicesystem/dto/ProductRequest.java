package com.yourcompany.invoicesystem.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;

/**
 * ProductRequest — inbound DTO for creating/updating a product.
 *
 * All money and percentage fields use BigDecimal — never double/float.
 * Validation annotations:
 *  - @Positive: price must be > 0
 *  - @Min(0): stockQuantity and gstPercent can be 0 but not negative
 *  - @Max(100): gstPercent can't exceed 100%
 */
@Data
public class ProductRequest {

    @NotBlank(message = "Product name is required")
    private String name;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    @NotNull(message = "GST percent is required")
    @Min(value = 0, message = "GST percent cannot be negative")
    @Max(value = 100, message = "GST percent cannot exceed 100")
    private BigDecimal gstPercent;
}
