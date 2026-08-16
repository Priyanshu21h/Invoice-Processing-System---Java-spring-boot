package com.yourcompany.invoicesystem.dto;

import lombok.Data;

/**
 * RegisterRequest — the JSON body sent by a client when creating a new account.
 *
 * Example request body:
 * {
 *   "username": "john",
 *   "email": "john@example.com",
 *   "password": "secret123",
 *   "role": "ROLE_USER"
 * }
 *
 * @Data (Lombok) generates: getters, setters, equals, hashCode, toString.
 * Kept as a plain DTO — no JPA, no business logic.
 */
@Data
public class RegisterRequest {
    private String username;
    private String email;
    private String password;
    private String role; // e.g. "ROLE_USER" or "ROLE_ADMIN"
}
