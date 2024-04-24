package com.potato.balbambalbam.user.signIn.service;

import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.user.signIn.dto.UserDto;
import com.potato.balbambalbam.data.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User saveUser(User user){
        return userRepository.save(user);
    }

    @Transactional
    public User updateUser(Long userId, UserDto userDto){
        User editUser = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("사용자를 찾을 수 없습니다."));
        if(userDto.getName()!=null){
            editUser.setName(userDto.getName());
        }
        if(userDto.getAge()!=null){
            editUser.setAge(userDto.getAge());
        }
        if(userDto.getGender()!=null){
            editUser.setGender(userDto.getGender());
        }
        return userRepository.save(editUser);
    }

    @Transactional
    public void deleteUser(Long id){
        userRepository.deleteById(id);
    }
}
