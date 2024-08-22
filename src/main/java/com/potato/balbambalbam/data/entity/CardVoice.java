package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@Entity(name = "card_voice")
@ToString
@NoArgsConstructor
public class CardVoice {
    @Id
    @Column(name = "id")
    private Long id;
    @Lob
    @Column(name = "child_male")
    private String childMale;
    @Lob
    @Column(name = "child_female")
    private String childFemale;
    @Lob
    @Column(name = "adult_male")
    private String adultMale;
    @Lob
    @Column(name = "adult_female")
    private String adultFemale;
    @Lob
    @Column(name = "elderly_male")
    private String elderlyMale;
    @Lob
    @Column(name = "elderly_female")
    private String elderlyFemale;

    @Builder
    public CardVoice(Long id, String childMale, String childFemale, String adultMale, String adultFemale, String elderlyMale, String elderlyFemale) {
        this.id = id;
        this.childMale = childMale;
        this.childFemale = childFemale;
        this.adultMale = adultMale;
        this.adultFemale = adultFemale;
        this.elderlyMale = elderlyMale;
        this.elderlyFemale = elderlyFemale;
    }
}
