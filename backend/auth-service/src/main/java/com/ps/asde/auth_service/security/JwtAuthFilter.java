package com.ps.asde.auth_service.security;

import com.ps.asde.auth_service.service.CustomUserDetailsService;
import com.ps.asde.auth_service.util.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {

    private final String SECRET_KEY = "Omesh12345678900000987654321Omesh";
    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getServletPath();
        log.info("Incoming request path: {}", path);

        if (path.startsWith("/auth/register") || path.startsWith("/auth/login")) {
            log.info("Skipping JWT filter for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }


        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Authorization header found or does not start with Bearer. Path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);
        log.info("Extracted JWT: {}", jwt);

        try {
            username = jwtService.extractUsername(jwt);
            log.info("Extracted username from JWT: {}", username);
        } catch (Exception e) {
            log.error("Error extracting username from JWT", e);
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
            log.info("Loaded UserDetails for username: {}", userDetails.getUsername());

            if (jwtService.isTokenValid(jwt, userDetails)) {
                log.info("JWT is valid for user: {}", username);

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()
                        );
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authToken);
                log.info("Authentication set for user: {}", username);
            } else {
                log.warn("Invalid JWT token for user: {}", username);
            }
        }

        filterChain.doFilter(request, response);
    }
}
