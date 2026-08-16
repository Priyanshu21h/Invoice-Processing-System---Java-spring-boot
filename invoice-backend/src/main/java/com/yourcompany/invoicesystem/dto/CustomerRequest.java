package com.yourcompany.invoicesystem.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * CustomerRequest — inbound DTO for creating/updating a customer.
 *
 * Validation annotations are evaluated when @Valid is on the controller parameter.
 * If any fail, Spring throws MethodArgumentNotValidException → our
 * GlobalExceptionHandler returns 400 with field-level error messages.
 */
@Data
public class CustomerRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be a valid email address")
    private String email;

    private String phone;

    private String address;
}
