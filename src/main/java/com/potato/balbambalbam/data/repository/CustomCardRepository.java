package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.CustomCard;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomCardRepository extends JpaRepository<CustomCard, Long> {

    List<CustomCard> findAllByUserId(Long userId);
}
