package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter @Setter
@ToString
public class VoiceRequestDto {
    public Integer age;
    public Integer gender;  //0 : 여자 , 1 : 남자

    public VoiceRequestDto(Integer age, Integer gender) {
        this.age = age;
        this.gender = gender;
    }
}
