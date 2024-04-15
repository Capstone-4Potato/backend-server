package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.Phoneme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhonemeRepository extends JpaRepository<Phoneme, Long> {

    Optional<Phoneme> findPhonemeByTypeAndText(Long type, String text);
    List<Phoneme> findPhonemeByTextOrderById(String text);
}
