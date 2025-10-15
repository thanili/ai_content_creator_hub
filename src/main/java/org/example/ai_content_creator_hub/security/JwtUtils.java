package org.example.ai_content_creator_hub.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for JWT operations: generating, parsing, and validating JWT tokens.
 */
@Component
public class JwtUtils {
    @Value("${org.example.project_management.jwt.secret}")
    private String secret;
    @Value("${org.example.project_management.jwt.accesss.expiration}")
    private String accessTokenExpirationTimeMs;
    @Value("${org.example.project_management.jwt.refresh.expiration}")
    private String refreshTokenExpirationTimeMs;
    @Value("${org.example.project_management.jwt.clockSkewSeconds}")
    private long clockSkewSeconds;

    private Key key;
    private JwtParser parser;

    @PostConstruct
    void init() {
        // Try Base64 first; if it fails, fall back to raw bytes.
        byte[] keyBytes;
        try {
            keyBytes = Decoders.BASE64.decode(secret);
        } catch (IllegalArgumentException ex) {
            // Not valid Base64 -> treat as plain text
            keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        }

        // HS384 needs ≈48+ bytes of key; fail fast if too short
        if (keyBytes.length < 48) {
            throw new IllegalStateException("JWT secret too short for HS384. Provide a ≥48-byte key (Base64 recommended).");
        }

        // For HS384, ensure at least 48-byte (~384-bit) key material for security.
        this.key = Keys.hmacShaKeyFor(keyBytes);

        this.parser = io.jsonwebtoken.Jwts.parserBuilder()
                .setSigningKey(this.key)
                .setAllowedClockSkewSeconds(clockSkewSeconds)
                .build();
    }

    /**
     * Generates a JWT access token for the given username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT access token
     */
    public String generateAccessToken(String username) {
        return generateToken(username, Integer.parseInt(accessTokenExpirationTimeMs));
    }

    /**
     * Generates a JWT refresh token for the given username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT refresh token
     */
    public String generateRefreshToken(String username) {
        return generateToken(username, Integer.parseInt(refreshTokenExpirationTimeMs));
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return getAllClaims(token).getSubject();
    }

    /**
     * Validates the given JWT token against the provided username.
     *
     * @param token the JWT token
     * @param username the username to validate against
     * @return true if the token is valid and matches the username, false otherwise
     */
    public boolean validateToken(String token, String username) {
        try {
            Claims claims = getAllClaims(token);
            final String actualUsername = claims.getSubject();
            return username.equals(actualUsername) && claims.getExpiration().after(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            // Signature invalid, token malformed/expired, etc.
            return false;
        }
    }

    private String generateToken(String subject, long expirationMs) {
        final Date now = new Date();
        final Date exp = new Date(now.getTime() + expirationMs);

        return io.jsonwebtoken.Jwts.builder()
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(this.key, io.jsonwebtoken.SignatureAlgorithm.HS384)
                .compact();
    }

    private Claims getAllClaims(String token) {
        return parser.parseClaimsJws(token).getBody();
    }

    /**
     * Checks if the given JWT token is expired.
     *
     * @param token the JWT token
     * @return true if the token is expired, false otherwise
     */
    private boolean isTokenExpired(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getExpiration().before(new Date());
    }
}
