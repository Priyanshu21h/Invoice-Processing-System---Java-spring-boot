package com.yourcompany.invoicesystem.dto;

import lombok.Data;

/**
 * LoginRequest — the JSON body sent by a client on the /api/auth/login endpoint.
 *
 * Example request body:
 * {
 *   "username": "john",
 *   "password": "secret123"
 * }
 *
 * We only need username + password for authentication.
 * The server will respond with a JwtResponse containing the signed token.
 */
@Data
public class LoginRequest {
    private String username;
    private String password;
}
