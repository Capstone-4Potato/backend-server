package com.potato.balbambalbam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.Map;

@Getter
@Entity(name = "card")
public class Card {
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "text", nullable = false)
    private String text;
    @Column(name = "pronunciation", nullable = false)
    private String pronunciation;
    @Column(name = "phonemes")
    @JdbcTypeCode(SqlTypes.JSON)
    private Map<String, Long> phonemesMap;
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    public Card(String text, String pronunciation, Map<String, Long> phonemesMap, Long categoryId){
        this.text = text;
        this.pronunciation = pronunciation;
        this.phonemesMap = phonemesMap;
        this.categoryId = categoryId;
    }

    public Card() {

    }
}
