package com.internship.tool.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import java.util.Collections;
import java.io.IOException;

public class JwtFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();

// ✅ Allow Swagger + Auth बिना token
if (path.startsWith("/auth") ||
    path.startsWith("/v3/api-docs") ||
    path.startsWith("/swagger-ui")) {

    filterChain.doFilter(request, response);
    return;
}
        String authHeader = request.getHeader("Authorization");

       if (authHeader == null || !authHeader.startsWith("Bearer ")) {
    filterChain.doFilter(request, response);
    return;
}

        try {
    String token = authHeader.substring(7);
    Claims claims = JwtUtil.validateToken(token);

    String role = (String) claims.get("role");

    // 🔥 THIS IS THE MISSING PART
    UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(
                    claims.getSubject(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role))
            );

    SecurityContextHolder.getContext().setAuthentication(auth);

    // Optional (your existing logic)
    request.setAttribute("role", role);

} catch (Exception e) {
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    return;
}

        filterChain.doFilter(request, response);
    }
}