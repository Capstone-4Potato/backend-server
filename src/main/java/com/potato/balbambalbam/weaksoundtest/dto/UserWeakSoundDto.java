package com.potato.balbambalbam.weaksoundtest.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserWeakSoundDto {
    private Long id;
    private Long userId;
    private String phonemeText;

    public UserWeakSoundDto(Long id, Long userID, String phonemeText){
        this.id = id;
        this.userId = userID;
        this.phonemeText = phonemeText;
    }
}
