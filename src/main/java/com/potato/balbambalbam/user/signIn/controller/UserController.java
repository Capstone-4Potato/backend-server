package com.potato.balbambalbam.user.signIn.controller;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.user.signIn.dto.UserDto;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.jwt.JWTUtil;
import com.potato.balbambalbam.user.signIn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTUtil jwtUtil;

    @GetMapping("/users")
    public ResponseEntity<?> checkSocialId(@RequestBody UserDto userDto) {
        try{Optional<User> userOptional = userRepository.findBySocialId(userDto.getSocialId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();
                String token = jwtUtil.createJwt(user.getName(), 3600000L);
                return ResponseEntity.ok().body(String.format("로그인이 완료되었습니다. Token: %s", token));
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("존재하지 않은 사용자 아이디입니다.");
            }
        }catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }

    }
    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        try {
            if (userDto.getName() == null || userDto.getAge() == null || userDto.getGender() == null || userDto.getSocialId() == null) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("입력 데이터가 충분하지 않습니다.");
            }

            User user = new User();
            user.setName(userDto.getName());
            user.setAge(userDto.getAge());
            user.setGender(userDto.getGender());
            user.setSocialId(userDto.getSocialId());

            User savedUser = userService.saveUser(user);
            String token = jwtUtil.createJwt(savedUser.getName(), 3600000L);

            return ResponseEntity.ok().body(String.format("회원가입이 완료되었습니다. Token: %s", token));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@RequestHeader("userId") Long userId, @RequestBody UserDto userDto) {
        try {
            userService.updateUser(userId, userDto);
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
            userService.deleteUser(userId);
            return ResponseEntity.ok().body("회원 탈퇴가 완료되었습니다.");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}
