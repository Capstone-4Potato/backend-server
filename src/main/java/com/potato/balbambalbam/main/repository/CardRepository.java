package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Override
    List<Card> findAll();
    List<Card> findAllByCategoryId(Long id);

    Optional<Card> findById(Long id);
}
