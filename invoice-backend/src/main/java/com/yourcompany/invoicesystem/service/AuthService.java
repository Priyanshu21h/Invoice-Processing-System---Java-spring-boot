package com.yourcompany.invoicesystem.service;

import com.yourcompany.invoicesystem.dto.JwtResponse;
import com.yourcompany.invoicesystem.dto.LoginRequest;
import com.yourcompany.invoicesystem.dto.RegisterRequest;
import com.yourcompany.invoicesystem.entity.User;
import com.yourcompany.invoicesystem.repository.UserRepository;
import com.yourcompany.invoicesystem.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * AuthService — business logic for user registration and login.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │
 * │  REGISTER flow:
 * │   1. Build a User entity from the request, BCrypt-hashing the password.
 * │      We NEVER store plain-text passwords.
 * │   2. Save the user to the DB via UserRepository.
 * │   3. Generate a JWT immediately so the user is "logged in" right after
 * │      registration (common UX pattern — no separate login step needed).
 * │   4. Return JwtResponse with the token, username, and role.
 * │
 * │  LOGIN flow:
 * │   1. Call authenticationManager.authenticate().
 * │      Internally this calls DaoAuthenticationProvider which:
 * │       - Loads the user from DB via CustomUserDetailsService.
 * │       - Calls passwordEncoder.matches(rawPassword, hashedPassword).
 * │       - Throws BadCredentialsException if credentials are wrong.
 * │   2. If authentication succeeds, load the User entity (to get the role).
 * │   3. Generate a JWT and return JwtResponse.
 * │
 * │  Why split AuthService from AuthController?
 * │   Separation of concerns: controller handles HTTP, service handles logic.
 * │   This makes AuthService independently testable without a web layer.
 * └────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    /**
     * register — creates a new user account and returns a JWT.
     *
     * @param request DTO containing username, email, password, role
     * @return JwtResponse with the generated token
     * @throws IllegalStateException if username or email is already taken
     */
    public JwtResponse register(RegisterRequest request) {
        // Guard against duplicate usernames / emails
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new IllegalStateException("Username already taken: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalStateException("Email already registered: " + request.getEmail());
        }

        // Build and persist the User entity
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .role(request.getRole())
                .build();

        userRepository.save(user);

        // Generate JWT so the user is immediately authenticated
        String token = jwtService.generateToken(user);
        return new JwtResponse(token, user.getUsername(), user.getRole());
    }

    /**
     * login — authenticates credentials and returns a JWT.
     *
     * @param request DTO containing username and password
     * @return JwtResponse with the generated token
     * @throws org.springframework.security.core.AuthenticationException on bad credentials
     */
    public JwtResponse login(LoginRequest request) {
        // Delegates to DaoAuthenticationProvider → verifies username + BCrypt password
        // Throws BadCredentialsException automatically if wrong
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                )
        );

        // Authentication succeeded — load the full User entity to build the response
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new IllegalStateException("User not found after authentication"));

        String token = jwtService.generateToken(user);
        return new JwtResponse(token, user.getUsername(), user.getRole());
    }
}
