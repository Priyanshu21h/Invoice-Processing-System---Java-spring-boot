package com.yourcompany.invoicesystem.exception;

/**
 * ResourceNotFoundException — thrown when a DB lookup returns empty.
 *
 * Examples: "Customer not found with id: 42", "Product not found with id: 7"
 *
 * The GlobalExceptionHandler maps this → HTTP 404.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience factory: ResourceNotFoundException.of("Customer", "id", 42)
     * → "Customer not found with id: 42"
     */
    public static ResourceNotFoundException of(String resource, String field, Object value) {
        return new ResourceNotFoundException(
                String.format("%s not found with %s: %s", resource, field, value));
    }
}
