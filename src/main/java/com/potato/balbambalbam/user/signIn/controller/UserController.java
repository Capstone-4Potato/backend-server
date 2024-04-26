package com.potato.balbambalbam.user.signIn.controller;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.user.signIn.dto.UserDto;
import com.potato.balbambalbam.data.repository.UserRepository;
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

    @PostMapping("/users")
    public ResponseEntity<?> createUser(@RequestBody UserDto userDto) {
        try {
            Optional<User> existData = userRepository.findByEmail(userDto.getEmail());
            if (existData.isPresent()) {
                return ResponseEntity
                        .status(HttpStatus.CONFLICT)
                        .body("이미 존재하는 이메일입니다.");
            }

            User user = new User();
            user.setName(userDto.getName());
            user.setAge(userDto.getAge());
            user.setGender(userDto.getGender());
            user.setEmail(userDto.getEmail());

            User savedUser = userService.saveUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }

    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@RequestHeader("userId") Long userId, @RequestBody UserDto userDto) {
        try {
            return new ResponseEntity<>(userService.updateUser(userId, userDto), HttpStatus.OK);
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
            return ResponseEntity.ok().build();
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다.");
        }
    }
}