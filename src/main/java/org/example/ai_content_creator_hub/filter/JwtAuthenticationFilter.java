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

    private boolean shouldSkip(String path) {
        return path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.equals("/swagger-ui.html")
                || path.startsWith("/h2-console")
                || path.startsWith("/api/auth")           // 👈 public auth endpoints
                || path.equals("/api/user/register");     // 👈 public register
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        final String path = request.getServletPath();

        if (shouldSkip(path)) {
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

        try {
            final String username = jwtUtil.extractUsername(jwt);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {
                    var auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    unauthorized(response, "Invalid JWT token");
                    return;
                }
            }

            chain.doFilter(request, response);
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "JWT token expired");
            return;
        } catch (io.jsonwebtoken.MalformedJwtException e) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "Malformed JWT token");
            return;
        } catch (Exception e) {
            SecurityContextHolder.clearContext();
            unauthorized(response, "JWT token error");
            return;
        }
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"" + message + "\"}");
        response.getWriter().flush();
    }
}
