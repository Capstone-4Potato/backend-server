package com.potato.balbambalbam.user.join.jwt;

import com.potato.balbambalbam.user.join.dto.CustomUserDetails;
import com.potato.balbambalbam.jwt.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.SneakyThrows;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        setFilterProcessesUrl("/login/social");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String socialId = request.getParameter("socialId");
        if (socialId == null) {
            throw new AuthenticationServiceException("socialId is required");
        }
        System.out.println(socialId);
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(socialId, "");

        return authenticationManager.authenticate(authRequest);
    }

    @SneakyThrows
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) {
        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        String socialId= customUserDetails.getUsername();

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", socialId, role, 600000L);
        System.out.println("access : " + access);
        String refresh = jwtUtil.createJwt("refresh", socialId, role, 86400000L);
        System.out.println("refresh : " + refresh);

        response.addHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));

        response.setContentType("application/json; charset=UTF-8"); // 명시적으로 UTF-8 인코딩 설정
        response.getWriter().write("{\"message\": \"로그인이 완료되었습니다.\", \"status\": " + HttpServletResponse.SC_OK + "}");
        response.getWriter().flush();
    }

    @SneakyThrows
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setContentType("application/json; charset=UTF-8"); // 명시적으로 UTF-8 인코딩 설정
        if (failed instanceof UsernameNotFoundException) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            response.getWriter().write("{\"message\": \"존재하지 않은 사용자 아이디입니다.\", \"status\": " + HttpServletResponse.SC_NOT_FOUND + "}");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("{\"message\": \"서버 오류가 발생했습니다.\", \"status\": " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR + "}");
        }
        response.getWriter().flush();
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }
}