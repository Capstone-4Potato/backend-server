package com.potato.balbambalbam.weaksoundtest.repository;

import com.potato.balbambalbam.entity.Phoneme;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PhonemeRepository extends JpaRepository<Phoneme, Long> {
}
