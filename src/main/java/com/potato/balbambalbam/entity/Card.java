package com.potato.balbambalbam.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.ToString;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.List;

@Getter
@Entity(name = "card")
@ToString
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
    private List<Long> phonemesMap;
    @Column(name = "category_id", nullable = false)
    private Long categoryId;

    public Card(String text, String pronunciation, List<Long> phonemesMap, Long categoryId){
        this.text = text;
        this.pronunciation = pronunciation;
        this.phonemesMap = phonemesMap;
        this.categoryId = categoryId;
    }

    public Card() {

    }
    public void setPhonemesMap(List<Long> phonemesMap) {
        this.phonemesMap = phonemesMap;
    }
}
