package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.CardScore;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CardScoreRepository extends JpaRepository<CardScore, Long> {

    Optional<CardScore> findByCardIdAndUserId(Long cardId, Long userId);

}
