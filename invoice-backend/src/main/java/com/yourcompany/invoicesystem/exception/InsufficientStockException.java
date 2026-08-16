package com.yourcompany.invoicesystem.exception;

/**
 * InsufficientStockException — thrown when an invoice requests more units
 * of a product than are currently available in inventory.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  This is a business-rule exception, not a data-integrity exception.
 * │  It's thrown BEFORE any stock is decremented (validate-all-first pattern).
 * │  The message includes product name, requested qty, and available qty
 * │  so the client/API consumer knows exactly what went wrong.
 * │
 * │  GlobalExceptionHandler maps this → HTTP 409 Conflict.
 * │  Why 409? The request conflicts with the current state of the resource
 * │  (the product's stock level). The client can retry after restocking.
 * └────────────────────────────────────────────────────────────────────────────
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productName,
                                      int requestedQty,
                                      int availableQty) {
        super(String.format(
                "Insufficient stock for product '%s': requested %d, available %d",
                productName, requestedQty, availableQty));
    }
}
