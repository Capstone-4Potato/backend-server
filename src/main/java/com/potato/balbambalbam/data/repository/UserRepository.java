package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email); //email로 사용자 조회
}
