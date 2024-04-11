package com.potato.balbambalbam.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity(name = "phoneme")
@Getter
@Setter
public class Phoneme {

    @Id
    private Long id;
    @Column(name = "type", nullable = false)
    private Long type;
    @Column(name = "text", nullable = false)
    private String text;

    public Phoneme() {
    }
}