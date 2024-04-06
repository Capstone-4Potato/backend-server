package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.CardBookmark;
import com.potato.balbambalbam.entity.CardBookmarkId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CardBookmarkRepository extends JpaRepository<CardBookmark, CardBookmarkId> {

    boolean existsByCardIdAndUserId(Long cardId, Long userId);
}
