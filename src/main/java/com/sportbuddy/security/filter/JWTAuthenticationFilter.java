package com.sportbuddy.security.filter;

import com.sportbuddy.model.Token;
import com.sportbuddy.repository.TokenRepository;
import com.sportbuddy.security.annotations.Public;
import com.sportbuddy.security.jwt.JWTService;
import com.sportbuddy.model.User;
import com.sportbuddy.repository.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JWTAuthenticationFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final UserRepository userRepository;
    private final TokenRepository tokenRepository; // <-- ADD THIS
    private final List<HandlerMapping> handlerMappings;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        // 1) Skip @Public endpoints
        if (isPublicEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        // 2) Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        String token = authHeader.substring(7);

        String email = jwtService.extractUsername(token);

        // 3) Validate token + check DB revoke
        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Load user
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // Load token record from DB
            Token tokenRecord = tokenRepository.findByAccessToken(token).orElse(null);

            // If token not found in DB → reject
            if (tokenRecord == null) {
                filterChain.doFilter(request, response);
                return;
            }

            // If token is revoked → reject
            if (tokenRecord.isRevoked()) {
                filterChain.doFilter(request, response);
                return;
            }

            // If token expired in DB → reject
            if (tokenRecord.getAccessExpiresAt().isBefore(LocalDateTime.now())) {
                filterChain.doFilter(request, response);
                return;
            }

            // Validate JWT signature
            if (jwtService.isTokenValid(token, user)) {

                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                user,
                                null,
                                user.getAuthorities()
                        );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }

    private boolean isPublicEndpoint(HttpServletRequest request) {
        try {
            for (HandlerMapping mapping : handlerMappings) {
                HandlerExecutionChain handler = mapping.getHandler(request);
                if (handler == null) continue;

                Object handlerObj = handler.getHandler();

                if (handlerObj instanceof HandlerMethod handlerMethod) {

                    // Method-level @Public
                    if (handlerMethod.hasMethodAnnotation(Public.class)) {
                        return true;
                    }

                    // Class-level @Public
                    if (handlerMethod.getBeanType().isAnnotationPresent(Public.class)) {
                        return true;
                    }
                }
            }
        } catch (Exception ignored) {}

        return false;
    }
}
