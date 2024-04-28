package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class AiTtsRequestDto {
    public Integer age;
    public Byte gender;  //0 : 여자 , 1 : 남자

    public AiTtsRequestDto(Integer age, Byte gender) {
        this.age = age;
        this.gender = gender;
    }
}
