package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CardRepository extends JpaRepository<Card, Long> {
    @Override
    List<Card> findAll();
}
