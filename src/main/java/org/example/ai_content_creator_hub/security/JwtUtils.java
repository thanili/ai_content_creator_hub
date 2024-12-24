package org.example.ai_content_creator_hub.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

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

    /**
     * Generates a JWT access token for the given username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT access token
     */
    public String generateAccessToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(accessTokenExpirationTimeMs)))
                .signWith(SignatureAlgorithm.HS384, secret)
                .compact();
    }

    /**
     * Generates a JWT refresh token for the given username.
     *
     * @param username the username for which the token is generated
     * @return the generated JWT refresh token
     */
    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + Integer.parseInt(refreshTokenExpirationTimeMs)))
                .signWith(SignatureAlgorithm.HS384, secret)
                .compact();
    }

    /**
     * Extracts the username from the given JWT token.
     *
     * @param token the JWT token
     * @return the username extracted from the token
     */
    public String extractUsername(String token) {
        return Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody().getSubject();
    }

    /**
     * Validates the given JWT token against the provided username.
     *
     * @param token the JWT token
     * @param username the username to validate against
     * @return true if the token is valid and matches the username, false otherwise
     */
    public boolean validateToken(String token, String username) {
        final String extractedUsername = extractUsername(token);
        return (extractedUsername.equals(username) && !isTokenExpired(token));
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
