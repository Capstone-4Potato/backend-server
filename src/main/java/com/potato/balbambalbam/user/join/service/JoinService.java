package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.Refresh;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.RefreshRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.InvalidUserNameException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.user.join.dto.JoinDTO;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class JoinService {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public JoinService(UserRepository userRepository, JWTUtil jwtUtil, RefreshRepository refreshRepository) {

        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this. refreshRepository = refreshRepository;
    }

    public String joinProcess(JoinDTO joinDTO, HttpServletResponse response) {

        String name = joinDTO.getName();
        String socialId = joinDTO.getSocialId();
        Integer age = joinDTO.getAge();
        Byte gender = joinDTO.getGender();

        Boolean isExist = userRepository.existsBySocialId(socialId);
        if (isExist) {
            return null;
        }

        User data = new User();

        data.setName(name);
        data.setSocialId(socialId);
        data.setAge(age);
        data.setGender(gender);
        data.setRole("ROLE_ADMIN");
        userRepository.save(data);

        String access = jwtUtil.createJwt("access", socialId, data.getRole(), 600000L);
        System.out.println("access : " + access);
        String refresh = jwtUtil.createJwt("refresh", socialId, data.getRole(), 86400000L);
        System.out.println("refresh : " + refresh);

        //refresh 토큰 저장
        addRefreshEntity(socialId, refresh, 86400000L);

        response.addCookie(createCookie("refresh", refresh));

        return access;
    }
    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        cookie.setHttpOnly(true);

        return cookie;
    }

    private void addRefreshEntity(String socialId, String refresh, Long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        Refresh refreshEntity = new Refresh();
        refreshEntity.setSocialId(socialId);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    @Transactional
    public User updateUser(Long userId, JoinDTO joinDTO) {
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));

        if (joinDTO.getSocialId() != null) {
            throw new RuntimeException("이메일 변경은 허용되지 않습니다.");
        }
        if (joinDTO.getName() != null) {
            editUser.setName(joinDTO.getName());
        }
        if (joinDTO.getAge() != null) {
            editUser.setAge(joinDTO.getAge());
        }
        if (joinDTO.getGender() != null) {
            editUser.setGender(joinDTO.getGender());
        }
        return userRepository.save(editUser);
    }

    @Transactional
    public void deleteUser(Long userId, String name){
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        if(!editUser.getName().equals(name)){
            throw new InvalidUserNameException("닉네임이 일치하지 않습니다.");
        }

        userRepository.deleteById(userId);
    }

    public Optional<User> findUserById(Long userId){
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        return userRepository.findById(userId);
    }
}