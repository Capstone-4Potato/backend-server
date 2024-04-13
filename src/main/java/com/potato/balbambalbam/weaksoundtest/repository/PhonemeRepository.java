package com.potato.balbambalbam.weaksoundtest.repository;

import com.potato.balbambalbam.entity.Phoneme;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PhonemeRepository extends JpaRepository<Phoneme, Long> {
    @Query("select p from phoneme p where p.type in :types and p.text = :text")
    List<Phoneme> findByTypeAndText(@Param("types") List<Long> types, @Param("text") String text);
}
