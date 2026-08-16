package com.yourcompany.invoicesystem.config;

import com.yourcompany.invoicesystem.security.JwtAuthFilter;
import com.yourcompany.invoicesystem.security.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.Customizer;

import java.util.List;

/**
 * SecurityConfig — the central Spring Security configuration for the
 * application.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │ @Configuration + @EnableWebSecurity tells Spring to use this class as the
 * │ security configuration instead of the auto-configured defaults.
 * │
 * │ Key decisions:
 * │ • STATELESS sessions: no HttpSession created. Every request must carry
 * │ its own JWT — the server keeps no memory of previous requests.
 * │ • CSRF disabled: CSRF attacks exploit session cookies. Since we're
 * │ stateless (no cookies, only JWT in Authorization header), CSRF doesn't
 * │ apply and disabling it avoids false rejections from REST clients.
 * │ • Public routes: /api/auth/** is open (login + register don't need a
 * token).
 * │ • Everything else requires authentication → results in 403 if no valid JWT.
 * │ • JwtAuthFilter is inserted BEFORE UsernamePasswordAuthenticationFilter
 * │ so JWT-authenticated requests are recognized before Spring tries
 * │ form-login logic.
 * │
 * │ DaoAuthenticationProvider:
 * │ Combines CustomUserDetailsService (load user from DB) with
 * │ BCryptPasswordEncoder (verify password hash).
 * │ AuthenticationManager delegates to this provider during login.
 * └────────────────────────────────────────────────────────────────────────────
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final CustomUserDetailsService customUserDetailsService;

    /**
     * SecurityFilterChain — defines URL authorization rules and the filter order.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())

                .cors(Customizer.withDefaults())

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**").permitAll()
                        .anyRequest().authenticated())

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * BCryptPasswordEncoder bean — used to hash passwords on register and
     * to verify them on login. BCrypt applies a salt automatically and is
     * intentionally slow, making brute-force attacks impractical.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider — wires together:
     * - CustomUserDetailsService: how to load the user from DB
     * - BCryptPasswordEncoder: how to verify the password
     *
     * Spring's AuthenticationManager uses this provider when authenticate() is
     * called.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * AuthenticationManager bean — exposed so AuthService can call
     * authManager.authenticate(new UsernamePasswordAuthenticationToken(...))
     * during the login flow.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("http://localhost:5173"));

        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        configuration.setAllowedHeaders(List.of("*"));

        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}
