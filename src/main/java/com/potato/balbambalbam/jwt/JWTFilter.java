package com.potato.balbambalbam.jwt;

import io.jsonwebtoken.io.DecodingException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JWTFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JWTFilter.class);
    private final JWTUtil jwtUtil;

    public JWTFilter(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        if (path.startsWith("/users")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = request.getHeader("Authorization");

        if (token == null) {
            log.info("Authorization token이 존재하지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        token = token.trim();

        if (token.startsWith("Bearer ")) {
            token = token.substring(7);
        } else {
            log.info("JWT token의 형식이 맞지 않습니다.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            if (jwtUtil.isExpired(token)) {
                log.info("토큰이 만료되었습니다.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("토큰이 만료되었습니다.");
                return;
            }

            String socialId = jwtUtil.getSocialId(token);
            if (socialId == null) {
                log.info("소셜 아이디가 없습니다.");
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                response.getWriter().write("소셜 아이디가 없습니다.");
                return;
            }

            Authentication authToken = new UsernamePasswordAuthenticationToken(socialId, null);
            SecurityContextHolder.getContext().setAuthentication(authToken);
        } catch (DecodingException e) {
            log.error("Error decoding JWT: {}", e.getMessage());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().write("Error decoding JWT: " + e.getMessage());
            return;
        }

        filterChain.doFilter(request, response);
    }
}