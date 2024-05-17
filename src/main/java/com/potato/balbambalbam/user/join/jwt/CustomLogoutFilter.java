package com.potato.balbambalbam.user.join.jwt;

import com.potato.balbambalbam.data.repository.RefreshRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

public class CustomLogoutFilter extends GenericFilterBean {

    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public CustomLogoutFilter(JWTUtil jwtUtil,
                              RefreshRepository refreshRepository) {
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public void doFilter(ServletRequest request,
                         ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request,
                          HttpServletResponse response,
                          FilterChain filterChain) throws IOException, ServletException {

        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        /*String refresh = null;
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        } else {
            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("쿠키가 없습니다.");
            return;
        }

        if (refresh == null) {
            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("refresh 토큰이 없습니다.");

            response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404
            return;
        }

        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("refresh 토큰이 만료되었습니다.");

            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED); //401
            return;
        }

        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("refresh 토큰이 아닙니다.");

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST); //400
            return;
        }

        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("refresh 토큰이 데이터베이스에 없습니다.");

            response.setStatus(HttpServletResponse.SC_REQUEST_TIMEOUT); //400
            return;
        }

        //로그아웃 진행
        refreshRepository.deleteByRefresh(refresh);

        //Refresh 토큰 Cookie 값 0
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");*/

        response.setContentType("text/plain; charset=UTF-8");
        response.getWriter().print("로그아웃이 완료되었습니다.");

        System.out.println("로그아웃이 완료되었습니다.");

        /*response.addCookie(cookie);*/
        response.setStatus(HttpServletResponse.SC_OK); //200
    }
}
