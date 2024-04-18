package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "user_weaksound")
@Getter
@Setter
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

    public UserWeakSound(Long userId, Long userPhoneme) {
        this.userId = userId;
        this.userPhoneme = userPhoneme;
    }
}
