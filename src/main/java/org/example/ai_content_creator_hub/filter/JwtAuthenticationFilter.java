package org.example.ai_content_creator_hub.filter;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.ai_content_creator_hub.security.JwtUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * A filter to intercept HTTP requests to perform JWT-based authentication.
 * Extends {@link OncePerRequestFilter} to ensure that it is executed once per request.
 *
 * This filter validates the JWT token received in the "Authorization" header,
 * extracts the username from the token, and authenticates the user if the token is valid.
 * If authentication is successful, the user's details are added to the {@link SecurityContextHolder}.
 *
 * The filter bypasses certain requests such as Swagger UI and OpenAPI documentation paths.
 *
 * It handles scenarios such as:
 * - Missing or malformed JWT tokens
 * - Expired tokens
 * - Validation against the user details service
 *
 * Dependencies:
 * - {@link JwtUtils} is used for extracting and validating JWT tokens.
 * - {@link UserDetailsService} is used to load user details based on the username extracted from the token.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtils jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String path = request.getServletPath();

        // Skip JWT filtering for Swagger UI and OpenAPI documentation paths
        if (path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs") || path.equals("/swagger-ui.html")) {
            chain.doFilter(request, response);
            return;
        }

        // Extract tokens from headers
        final String authorizationHeader = request.getHeader("Authorization");

        // If no Authorization header or doesn't start with "Bearer", skip authentication
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            chain.doFilter(request, response);
            return;
        }

        String jwt = authorizationHeader.substring(7); // Extract JWT token after "Bearer "
        String username = null;

        try {
            // Attempt to extract username from the token
            username = jwtUtil.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired", e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("JWT Token is expired. Please refresh your token: " + e.getMessage());
            return;
        } catch (MalformedJwtException e) {
            logger.error("Malformed JWT token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid JWT token");
            return;
        } catch (Exception e) {
            logger.error("Invalid JWT token", e);
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "JWT token error");
            return;
        }

        // If username is found, check for authentication
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Validate the token and authenticate if valid
            if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        chain.doFilter(request, response);
    }
}
