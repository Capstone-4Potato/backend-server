package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.user.join.dto.DeleteUserDto;
import com.potato.balbambalbam.user.join.dto.EditDto;
import com.potato.balbambalbam.user.join.dto.JoinDto;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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

    // 회원정보 받기
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@Validated @RequestBody JoinDto joinDto, HttpServletResponse response) {

        String access = joinService.joinProcess(joinDto, response);
        response.setHeader("access", access);

        System.out.println("회원가입이 완료되었습니다.");
        return ResponseEntity.ok().body("회원가입이 완료되었습니다."); //200
    }

    // 회원정보 수정
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@Validated @RequestHeader("access") String access, @RequestBody JoinDto joinDto) {

        Long userId = extractUserIdFromToken(access);
        EditDto editUser = joinService.updateUser(userId, joinDto);

        System.out.println( userId + " : 사용자 정보가 수정되었습니다.");
        return ResponseEntity.ok().body(editUser); //200
    }

    //회원정보 삭제
    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("access") String access, @RequestBody DeleteUserDto deleteUserDto,
                                        HttpServletRequest request, HttpServletResponse response) {

        Long userId = extractUserIdFromToken(access);
        String name = deleteUserDto.getName();
        joinService.deleteUser(userId, name);

        // refresh
        /*if (refresh != null && refreshRepository.existsByRefresh(refresh)) {
            refreshRepository.deleteByRefresh(refresh);
        }*/

        System.out.println( userId + " : 사용자 정보가 삭제되었습니다.");
        return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다."); //200
    }

    //회원정보 출력
    @GetMapping("/users")
    public ResponseEntity<?> getUserById(@RequestHeader("access") String access) {

        Long userId = extractUserIdFromToken(access);
        EditDto editUser = joinService.findUserById(userId);

        System.out.println( userId + " : 사용자 정보를 전송했습니다.");
        return ResponseEntity.ok().body(editUser); //200
    }

}
