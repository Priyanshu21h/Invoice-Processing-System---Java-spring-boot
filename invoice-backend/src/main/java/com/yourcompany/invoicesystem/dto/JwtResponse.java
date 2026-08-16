package com.yourcompany.invoicesystem.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * JwtResponse — the JSON body returned to the client after successful
 * registration or login.
 *
 * Example response:
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "username": "john",
 *   "role": "ROLE_USER"
 * }
 *
 * The client stores this token (typically in localStorage or memory) and
 * sends it in the Authorization header on every subsequent request:
 *   Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
 *
 * @AllArgsConstructor makes it easy to build: new JwtResponse(token, username, role)
 * @NoArgsConstructor is needed for Jackson deserialization (if ever consumed as input).
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponse {
    private String token;
    private String username;
    private String role;
}
