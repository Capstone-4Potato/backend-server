package com.potato.balbambalbam.user.join.jwt;

import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.user.join.dto.CustomUserDetails;
import jakarta.servlet.FilterChain;
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

import java.io.PrintWriter;
import java.util.Collection;
import java.util.Iterator;

public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public LoginFilter(AuthenticationManager authenticationManager, JWTUtil jwtUtil, RefreshRepository refreshRepository) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String socialId = request.getParameter("socialId");
        if (socialId == null) {
            throw new AuthenticationServiceException("socialId가 없습니다.");
        }
        /*System.out.println("social id : " + socialId);*/
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

        //토큰 생성
        String access = jwtUtil.createJwt("access", socialId, role, 7200000L); //2시간

        System.out.println("access 토큰이 발급되었습니다.");

       /* String refresh = jwtUtil.createJwt("refresh", socialId, role, 86400000L); //24시간
        System.out.println("refresh : " + refresh);*/

        //refresh 토큰 저장
        /*addRefreshEntity(socialId, refresh, 86400000L);*/

        response.setHeader("access", access);
        /*response.addCookie(createCookie("refresh", refresh));*/

        response.setContentType("text/plain; charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print("로그인이 완료되었습니다.");

        System.out.println("로그인이 완료되었습니다.");
    }

    @SneakyThrows
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
        response.setContentType("application/json; charset=UTF-8"); // 명시적으로 UTF-8 인코딩 설정
        if (failed instanceof UsernameNotFoundException) {

            response.setStatus(HttpServletResponse.SC_NOT_FOUND); //404

            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("존재하지 않은 사용자 아이디입니다.");

            System.out.println("로그인에 실패하였습니다.");
        } else {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); //500

            response.setContentType("text/plain; charset=UTF-8");
            PrintWriter writer = response.getWriter();
            writer.print("서버 오류가 발생했습니다.");
        }

        response.getWriter().flush();
    }

    /*private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);
        cookie.setPath("/");

        return cookie;
    }*/

   /* private void addRefreshEntity(String socialId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }*/
}