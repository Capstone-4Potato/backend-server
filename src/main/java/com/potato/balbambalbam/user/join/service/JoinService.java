package com.potato.balbambalbam.user.join.service;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.user.join.dto.JoinDTO;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class JoinService {

    private final UserRepository userRepository;

    public JoinService(UserRepository userRepository) {

        this.userRepository = userRepository;
    }

    public void joinProcess(JoinDTO joinDTO) {

        String name = joinDTO.getName();
        String socialId = joinDTO.getSocialId();
        Integer age = joinDTO.getAge();
        Byte gender = joinDTO.getGender();

        Boolean isExist = userRepository.existsBySocialId(socialId);

        if (isExist) {
            return;
        }

        User data = new User();

        data.setName(name);
        data.setSocialId(socialId);
        data.setAge(age);
        data.setGender(gender);
        data.setRole("ROLE_ADMIN");

        userRepository.save(data);
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
    public void deleteUser(Long userId){
        User editUser = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("사용자를 찾을 수 없습니다."));
        userRepository.deleteById(userId);
    }
}