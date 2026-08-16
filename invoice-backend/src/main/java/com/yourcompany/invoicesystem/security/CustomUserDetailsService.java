package com.yourcompany.invoicesystem.security;

import com.yourcompany.invoicesystem.entity.User;
import com.yourcompany.invoicesystem.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * CustomUserDetailsService — bridges our User entity with Spring Security.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  Spring Security doesn't know about our User entity.  It works with a
 * │  UserDetails interface.  This service is the adapter:
 * │
 * │    DB User entity  ──loadUserByUsername()──▶  Spring UserDetails
 * │
 * │  Spring Security calls loadUserByUsername() in two places:
 * │   1. During login (via AuthenticationManager / DaoAuthenticationProvider)
 * │   2. In our JwtAuthFilter when we validate an incoming request's token
 * │
 * │  GrantedAuthority represents a permission/role.  We wrap our plain
 * │  "ROLE_ADMIN" string in a SimpleGrantedAuthority so Spring understands it.
 * └────────────────────────────────────────────────────────────────────────────
 */
@Service
@RequiredArgsConstructor  // Lombok generates a constructor injecting all final fields
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     * loadUserByUsername — look up user in the database by username.
     *
     * Returns a Spring Security UserDetails object built from our User entity:
     *  - username  → user.getUsername()
     *  - password  → user.getPassword()  (BCrypt hash stored in DB)
     *  - authorities → [SimpleGrantedAuthority("ROLE_ADMIN")] (or ROLE_USER, etc.)
     *
     * Spring Security uses the returned UserDetails to:
     *  - Compare the provided password hash during login
     *  - Set up the SecurityContext for the duration of the request
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username: " + username));

        // Map our plain role string → Spring's GrantedAuthority
        SimpleGrantedAuthority authority = new SimpleGrantedAuthority(user.getRole());

        // org.springframework.security.core.userdetails.User (Spring's built-in UserDetails impl)
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                List.of(authority)
        );
    }
}
