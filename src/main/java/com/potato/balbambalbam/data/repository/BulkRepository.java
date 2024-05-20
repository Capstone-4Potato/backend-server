package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CardWeakSound;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.List;

@Repository
@Slf4j
@RequiredArgsConstructor
public class BulkRepository {
    @PersistenceContext
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;

    @Transactional
    public void saveAll(List<CardWeakSound> cardWeakSoundList){
        String sql = "INSERT INTO card_weaksound (user_id, card_id) VALUES (?, ?)";

        jdbcTemplate.batchUpdate(sql,
                cardWeakSoundList,
                cardWeakSoundList.size(),
                (PreparedStatement ps, CardWeakSound cardWeakSound) -> {
                    ps.setLong(1, cardWeakSound.getUserId());
                    ps.setLong(2, cardWeakSound.getCardId());
                });

        //영속성 초기화
        entityManager.clear();
    }

    @Transactional
    public void deleteAllByUserId(Long userId){
        String sql = "DELETE FROM card_weaksound WHERE user_id = ?";

        int count = jdbcTemplate.update(sql, userId);

        //영속성 초기화
        entityManager.clear();
    }
}
