package com.potato.balbambalbam.entity;

import jakarta.persistence.*;
import lombok.Getter;

@Entity(name = "user_weaksound")
@Getter
public class UserWeakSound {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @Column(name = "user_phoneme", nullable = false)
    private Long userPhoneme;

    public UserWeakSound() {
    }
}
