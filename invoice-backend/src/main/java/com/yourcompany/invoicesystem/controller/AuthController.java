package com.yourcompany.invoicesystem.controller;

import com.yourcompany.invoicesystem.dto.JwtResponse;
import com.yourcompany.invoicesystem.dto.LoginRequest;
import com.yourcompany.invoicesystem.dto.RegisterRequest;
import com.yourcompany.invoicesystem.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * AuthController — exposes the two public authentication endpoints.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  @RestController = @Controller + @ResponseBody.  Every method's return
 * │  value is automatically serialized to JSON by Jackson.
 * │
 * │  Both endpoints are under /api/auth/** which SecurityConfig permits
 * │  without a JWT, so these are accessible to unauthenticated clients.
 * │
 * │  POST /api/auth/register  → 201 Created  + JwtResponse body
 * │  POST /api/auth/login     → 200 OK       + JwtResponse body
 * │
 * │  The controller is intentionally thin — it only handles HTTP concerns
 * │  (request parsing, status codes) and delegates all logic to AuthService.
 * │
 * │  Error handling: if AuthService throws (e.g. duplicate username or
 * │  bad credentials) we rely on a global @ControllerAdvice for consistent
 * │  error responses (to be added later).
 * └────────────────────────────────────────────────────────────────────────────
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * POST /api/auth/register
     *
     * Request body (JSON):
     * {
     *   "username": "john",
     *   "email": "john@example.com",
     *   "password": "secret123",
     *   "role": "ROLE_USER"
     * }
     *
     * Response 201 Created:
     * {
     *   "token": "eyJhbGci...",
     *   "username": "john",
     *   "role": "ROLE_USER"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<JwtResponse> register(@RequestBody RegisterRequest request) {
        JwtResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * POST /api/auth/login
     *
     * Request body (JSON):
     * {
     *   "username": "john",
     *   "password": "secret123"
     * }
     *
     * Response 200 OK:
     * {
     *   "token": "eyJhbGci...",
     *   "username": "john",
     *   "role": "ROLE_USER"
     * }
     *
     * Returns 403/401 automatically via Spring Security if credentials are wrong.
     */
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody LoginRequest request) {
        JwtResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }
}
