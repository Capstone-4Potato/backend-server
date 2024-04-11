package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class VoiceRequestDto {
    public Integer age;
    public Integer gender;

    public VoiceRequestDto(Integer age, Integer gender) {
        this.age = age;
        this.gender = gender;
    }
}
