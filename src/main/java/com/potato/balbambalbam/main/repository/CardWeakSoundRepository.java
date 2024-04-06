package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.CardWeakSound;
import com.potato.balbambalbam.entity.CardWeakSoundId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardWeakSoundRepository extends JpaRepository<CardWeakSound, CardWeakSoundId> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);
}
