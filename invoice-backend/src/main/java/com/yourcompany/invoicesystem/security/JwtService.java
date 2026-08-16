package com.yourcompany.invoicesystem.security;

import com.yourcompany.invoicesystem.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JwtService — the single class responsible for all JWT operations.
 *
 * ┌─ INTERVIEW EXPLANATION ────────────────────────────────────────────────────
 * │  A JWT has 3 parts separated by dots:  Header . Payload . Signature
 * │
 * │  • Header  : {"alg":"HS256","typ":"JWT"}  (base64-encoded)
 * │  • Payload : Claims — subject (username), role, iat (issued-at), exp (expiry)
 * │  • Signature: HMAC-SHA256( base64(Header) + "." + base64(Payload) , secretKey )
 * │
 * │  On every incoming request the server re-computes the signature with its
 * │  own secret and compares it to the token's signature.  If they differ
 * │  (tampered data or wrong secret) → parsing throws SignatureException → 401.
 * │
 * │  Why stateless?  The server doesn't store sessions.  All info needed for
 * │  auth lives inside the token itself (self-contained).
 * └────────────────────────────────────────────────────────────────────────────
 */
@Service
public class JwtService {

    /** Secret key string read from application.properties (jwt.secret). */
    @Value("${jwt.secret}")
    private String secret;

    /** Token lifetime in milliseconds read from application.properties (jwt.expiration). */
    @Value("${jwt.expiration}")
    private long expiration;

    // ── Signing Key ───────────────────────────────────────────────────────────

    /**
     * Converts the raw secret string into a cryptographic Key object.
     * HS256 requires at least 256 bits (32 bytes); our secret is 62 chars so it's fine.
     */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Token Generation ──────────────────────────────────────────────────────

    /**
     * generateToken — builds and signs a JWT for the given User.
     *
     * Claims embedded:
     *  - "role"  → e.g. "ROLE_ADMIN"   (custom claim, needed for authorization)
     *  - subject → username             (standard claim, identifies who this token belongs to)
     *  - iat     → issued-at timestamp
     *  - exp     → current time + expiration millis
     */
    public String generateToken(User user) {
        Map<String, Object> extraClaims = new HashMap<>();
        extraClaims.put("role", user.getRole());

        return Jwts.builder()
                .setClaims(extraClaims)                                         // custom claims FIRST
                .setSubject(user.getUsername())                                 // sub = username
                .setIssuedAt(new Date(System.currentTimeMillis()))              // iat
                .setExpiration(new Date(System.currentTimeMillis() + expiration)) // exp
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)            // sign
                .compact();                                                     // serialize to String
    }

    // ── Claim Extraction ──────────────────────────────────────────────────────

    /**
     * Parses the JWT and returns the full Claims (payload) object.
     * NOTE: parseClaimsJws() validates the signature automatically —
     * if the signature is invalid it throws JwtException before returning.
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /** Generic helper — apply any function to the Claims to pull out one field. */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        return claimsResolver.apply(extractAllClaims(token));
    }

    /**
     * Extracts the "sub" (subject) claim — which we set to username at token creation.
     * This is what we use to look up the user in the DB when a request arrives.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extracts our custom "role" claim.
     * Used for role-based authorization checks.
     */
    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // ── Token Validation ─────────────────────────────────────────────────────

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * isTokenValid — two-part check:
     *  1. Username in token == username in the UserDetails loaded from DB
     *     (guards against tokens issued to a different user being re-used)
     *  2. Token has not expired
     *
     * Signature validity is checked implicitly by extractAllClaims() above.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return username.equals(userDetails.getUsername()) && !isTokenExpired(token);
    }
}
