package com.potato.balbambalbam.user.join.controller;

import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.user.join.dto.JoinDTO;
import com.potato.balbambalbam.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@ResponseBody
public class JoinController {

    private final JoinService joinService;
    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;

    public JoinController(JoinService joinService, UserRepository userRepository, JWTUtil jwtUtil) {

        this.joinService = joinService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody JoinDTO joinDto) {
        try {
            if (joinDto.getName() == null || joinDto.getAge() == null || joinDto.getGender() == null || joinDto.getSocialId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력 데이터가 충분하지 않습니다.");
            }
            joinService.joinProcess(joinDto);
            return ResponseEntity.ok().body(String.format("회원가입이 완료되었습니다."));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@RequestHeader("userId") Long userId, @RequestBody JoinDTO joinDto) {
        try {
            joinService.updateUser(userId, joinDto);
            return ResponseEntity.ok().body("회원정보 수정이 완료되었습니다.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestHeader("userId") Long userId) {
        try {
            joinService.deleteUser(userId);
            return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
