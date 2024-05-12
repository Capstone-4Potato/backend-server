package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.exception.InvalidUserNameException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.join.dto.DeleteUserDto;
import com.potato.balbambalbam.user.join.dto.JoinDTO;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;
    private final RefreshRepository refreshRepository;
    private final JWTUtil jwtUtil;

    public JoinController(JoinService joinService,
                          JWTUtil jwtUtil,
                          RefreshRepository refreshRepository) {
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
        this.refreshRepository = refreshRepository;
    }

    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody JoinDTO joinDto, HttpServletResponse response) {
        try {
            if (joinDto.getName() == null || joinDto.getAge() == null || joinDto.getGender() == null || joinDto.getSocialId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력 데이터가 충분하지 않습니다.");
            }
            System.out.println(joinDto.getName());
            System.out.println(joinDto.getAge());
            System.out.println(joinDto.getGender());
            System.out.println(joinDto.getSocialId());

            String access = joinService.joinProcess(joinDto, response);
            response.setHeader("access", access);

            return ResponseEntity.status(HttpStatus.OK).body("회원가입이 완료되었습니다."); //200
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); //500
        }
    }
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@RequestHeader("access") String access, @RequestBody JoinDTO joinDto) {
        try {
            Long userId = extractUserIdFromToken(access);
            Optional<User> user = joinService.updateUser(userId, joinDto);
            return ResponseEntity.ok().body(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //404 사용자를 찾을 수 없습니다.
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); //400 이메일 변경은 허용되지 않습니다.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); //500
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("access") String access,
                                        @RequestBody DeleteUserDto deleteUserDto,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {
        String name = deleteUserDto.getName();
        try {
            Long userId = extractUserIdFromToken(access);
            joinService.deleteUser(userId, name);

            /*String refresh = null;
            Cookie[] cookies = request.getCookies();
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }

            if (refresh != null) {
                refreshRepository.deleteByRefresh(refresh);
                deleteCookie(response, "refresh");
            }*/

            return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다."); //200
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //404 사용자를 찾을 수 없습니다.
        } catch (InvalidUserNameException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage()); // 400 닉네임이 일치하지 않습니다.
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    /*private void deleteCookie(HttpServletResponse response, String cookieName) {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0); // 만료 시간 0으로 설정하여 쿠키 삭제
        response.addCookie(cookie);
    }*/

    @GetMapping("/users")
    public ResponseEntity<?> getUserById(@RequestHeader("access") String access) {
        try {
            Long userId = extractUserIdFromToken(access);
            Optional<User> user = joinService.findUserById(userId);
            return ResponseEntity.ok().body(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); //404
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); //500
        }
    }


}
