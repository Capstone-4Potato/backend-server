package com.potato.balbambalbam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import lombok.Getter;

import java.io.Serializable;

@Entity(name = "card_bookmark")
@Getter
@IdClass(CardBookmark.class)
public class CardBookmark implements Serializable {

    @Id
    @Column(name = "user_id")
    private Long userId;
    @Id
    @Column(name = "card_id")
    private Long cardId;

    public CardBookmark() {

    }
}
