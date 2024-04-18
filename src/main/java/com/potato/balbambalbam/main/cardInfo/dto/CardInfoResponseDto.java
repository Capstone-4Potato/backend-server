package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor
public class CardInfoResponseDto {
    private Long id;
    private String text;
    private String pronunciation;
    private boolean isBookmark;
    private String ttsVoice;
}
