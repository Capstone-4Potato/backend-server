package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.*;

@Getter @Setter
@ToString
@NoArgsConstructor
public class AiTtsRequestDto {
    public Integer age;
    public Byte gender;  //0 : 여자 , 1 : 남자

    public String text;

    public AiTtsRequestDto(Integer age, Byte gender, String text) {
        this.age = age;
        this.gender = gender;
        this.text = text;
    }
}
