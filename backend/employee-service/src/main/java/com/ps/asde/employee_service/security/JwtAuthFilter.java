package com.ps.asde.employee_service.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    private static final String SECRET_KEY = "Omesh12345678900000987654321Omesh";
    private static final Logger log = LoggerFactory.getLogger(JwtAuthFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        log.info("Authorization header: {}", authHeader);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Missing or invalid Authorization header");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        String jwt = authHeader.substring(7);
        try {
            JwtParser parser = Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY.getBytes())
                    .build();

            Claims claims = parser.parseClaimsJws(jwt).getBody();
            Long userId = Long.valueOf(claims.get("sub").toString());
            String username = claims.get("username", String.class);
            String role = claims.get("role", String.class);
            Long employeeId = Long.valueOf(claims.get("employeeId").toString());
            Long managerId = Long.valueOf(claims.get("managerId").toString());

            log.info("JWT parsed successfully: username={}, role={}, employeeId={}, managerId={}",
                    username, role, employeeId, managerId);

            request.setAttribute("userId", userId);
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            request.setAttribute("employeeId", employeeId);
            request.setAttribute("managerId", managerId);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                var authorities = Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(username, null, authorities);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.info("Spring Security context updated with authentication");
            }

        } catch (Exception e) {
            log.error("JWT parsing/validation failed: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
