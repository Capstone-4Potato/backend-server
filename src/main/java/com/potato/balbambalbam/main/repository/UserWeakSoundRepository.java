package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.UserWeakSound;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserWeakSoundRepository extends JpaRepository<UserWeakSound, Long> {

    List<UserWeakSound> findAllByUserId(Long userId);

}
