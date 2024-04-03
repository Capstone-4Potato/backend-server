package com.potato.balbambalbam.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "card_score")
@Getter
public class CardScore {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "highest_score", nullable = false)
    private Integer highestScore;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "card_id", nullable = false)
    private Long cardId;

    public CardScore() {
    }
}
