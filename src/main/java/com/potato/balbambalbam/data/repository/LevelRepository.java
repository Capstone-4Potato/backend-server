package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Level;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LevelRepository extends JpaRepository<Level, Long> {
    Level findByLevelId(Long levelId);
}
