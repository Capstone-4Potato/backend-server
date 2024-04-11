package com.potato.balbambalbam.main.cardInfo.dto;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
public class CardInfoResponseDto {
    private Long id;
    private String text;
    private String pronunciation;
    private String ttsVoice;
}
