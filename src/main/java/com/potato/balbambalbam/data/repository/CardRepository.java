package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.SequencedCollection;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Override
    List<Card> findAll();
    List<Card> findAllByCategoryId(Long id);
    List<Card> findByCategoryIdIn(List<Long> categoryIds);
    Optional<Card> findByCardId(Long id);
    List<Card> findByTextOrderByCardId(String text);
    @Query(value = "select * from card c where c.card_id not in (select cv.card_id from card_voice cv)", nativeQuery = true)
    List<Card> findCardNotInCardVoice();

}