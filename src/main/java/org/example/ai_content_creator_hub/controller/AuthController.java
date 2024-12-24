package org.example.ai_content_creator_hub.controller;

import jakarta.validation.Valid;
import org.example.ai_content_creator_hub.dto.auth.AuthRequest;
import org.example.ai_content_creator_hub.dto.auth.AuthResponse;
import org.example.ai_content_creator_hub.dto.auth.RefreshTokenResponse;
import org.example.ai_content_creator_hub.security.CustomUserDetailsService;
import org.example.ai_content_creator_hub.security.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for authentication-related operations, including user login and token refreshing.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthenticationManager authenticationManager;
    private final CustomUserDetailsService userDetailsService;
    private final JwtUtils jwtUtils;

    @Autowired
    public AuthController(AuthenticationManager authenticationManager,
                          CustomUserDetailsService userDetailsService,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtUtils = jwtUtils;
    }

    /**
     * Authenticates a user based on the provided credentials and returns access and refresh tokens.
     *
     * @param authRequest the authentication request containing the username and password
     * @return a ResponseEntity containing either an {@link AuthResponse} with the tokens upon successful
     *         authentication or an error message with the appropriate HTTP status code
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody AuthRequest authRequest) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));

            UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
            String accessToken = jwtUtils.generateAccessToken(userDetails.getUsername());
            String refreshToken = jwtUtils.generateRefreshToken(userDetails.getUsername());

            return ResponseEntity.ok(new AuthResponse(accessToken, refreshToken));
        } catch (AuthenticationException e) {
            logger.error("Authentication failed", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Failed to generate token", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Failed to generate token: " + e.getMessage());
        }
    }

    /**
     * Refreshes the access token using the provided refresh token. Validates the refresh token,
     * extracts user information, and generates a new access token if valid.
     *
     * @param refreshToken the refresh token provided in the request header for token renewal
     * @return a ResponseEntity containing a {@link RefreshTokenResponse} with the new access token
     *         if the refresh token is valid, or an error message with an appropriate HTTP status code
     *         if the token is invalid or processing fails
     */
    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestHeader("Refresh-Token") String refreshToken) {
        try {
            // Validate the refresh token and generate a new access token
            String username = jwtUtils.extractUsername(refreshToken);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            if (jwtUtils.validateToken(refreshToken, userDetails.getUsername())) {
                String newAccessToken = jwtUtils.generateAccessToken(userDetails.getUsername()); // Generate new token
                return ResponseEntity.ok(new RefreshTokenResponse(newAccessToken));
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid refresh token");
            }
        } catch (Exception ex) {
            logger.error("Failed to refresh token", ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Failed to refresh token");
        }
    }
}
