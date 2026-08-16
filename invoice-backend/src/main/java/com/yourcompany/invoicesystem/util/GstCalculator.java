package com.yourcompany.invoicesystem.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * GstCalculator — utility class for all money and tax calculations.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  Why BigDecimal? Never use double or float for currency! Floating-point
 * │  math can introduce small precision errors (e.g., 0.1 + 0.2 = 0.30000000000000004).
 * │  BigDecimal ensures exact decimal arithmetic.
 * │
 * │  We strictly apply setScale(2, RoundingMode.HALF_UP) everywhere to round
 * │  money to 2 decimal places using standard commercial rounding.
 * └────────────────────────────────────────────────────────────────────────────
 */
public class GstCalculator {

    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
    private static final BigDecimal ONE_HUNDRED = new BigDecimal("100");

    /**
     * Calculates the GST amount for a line item.
     * Formula: (price * quantity) * (gstPercent / 100)
     */
    public static BigDecimal calculateLineGst(BigDecimal price, int quantity, BigDecimal gstPercent) {
        BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
        BigDecimal gstFraction = gstPercent.divide(ONE_HUNDRED, 4, ROUNDING_MODE); // 4 scale for intermediate division
        return subtotal.multiply(gstFraction).setScale(SCALE, ROUNDING_MODE);
    }

    /**
     * Calculates the discount amount for a given subtotal.
     * Formula: subtotal * (discountPercent / 100)
     */
    public static BigDecimal calculateDiscount(BigDecimal subtotal, BigDecimal discountPercent) {
        if (discountPercent == null || discountPercent.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        }
        BigDecimal discountFraction = discountPercent.divide(ONE_HUNDRED, 4, ROUNDING_MODE);
        return subtotal.multiply(discountFraction).setScale(SCALE, ROUNDING_MODE);
    }
    
    /**
     * Utility method to enforce our standard scale/rounding on any BigDecimal.
     */
    public static BigDecimal standardize(BigDecimal amount) {
        if (amount == null) return BigDecimal.ZERO.setScale(SCALE, ROUNDING_MODE);
        return amount.setScale(SCALE, ROUNDING_MODE);
    }
}
