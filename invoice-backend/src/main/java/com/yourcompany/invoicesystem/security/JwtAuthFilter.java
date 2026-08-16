package com.yourcompany.invoicesystem.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JwtAuthFilter — intercepts every HTTP request exactly once and, if a valid
 * JWT is present, populates the Spring Security SecurityContext.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  Spring Security has a chain of filters every request passes through.
 * │  This filter sits BEFORE UsernamePasswordAuthenticationFilter.
 * │
 * │  What it does per request:
 * │  1. Look for the "Authorization" header.
 * │  2. If missing / doesn't start with "Bearer " → skip (pass to next filter).
 * │     This is correct — unauthenticated requests to public endpoints are fine.
 * │  3. Extract the token, pull out the username from the JWT payload.
 * │  4. If SecurityContext is empty (not already authenticated this request):
 * │       a. Load UserDetails from DB via CustomUserDetailsService.
 * │       b. Validate the token (username match + not expired).
 * │       c. Create a UsernamePasswordAuthenticationToken and put it in
 * │          SecurityContext → Spring now treats this request as authenticated.
 * │  5. Continue the filter chain.
 * │
 * │  OncePerRequestFilter guarantees this runs exactly once per request,
 * │  even in forward/include chains.
 * └────────────────────────────────────────────────────────────────────────────
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // ── Step 1: Read the Authorization header ─────────────────────────────
        final String authHeader = request.getHeader("Authorization");

        // ── Step 2: If no header or not a Bearer token → skip this filter ─────
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 3: Extract the JWT (everything after "Bearer ") ─────────────
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception e) {
            // Malformed or tampered token → don't authenticate, but don't throw —
            // let the request proceed unauthenticated; Spring Security will reject it.
            filterChain.doFilter(request, response);
            return;
        }

        // ── Step 4: Authenticate if not already done for this request ─────────
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load the user's full details (authorities, etc.) from DB
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate: username matches + token not expired
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Build an authenticated token — credentials=null because we don't need
                // the password anymore (JWT already proved identity).
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );

                // Attach HTTP request details (IP address, session ID, etc.) for auditing
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Put the authenticated token into the SecurityContext —
                // from this point in the filter chain the request is "logged in"
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // ── Step 5: Always continue the filter chain ──────────────────────────
        filterChain.doFilter(request, response);
    }
}
