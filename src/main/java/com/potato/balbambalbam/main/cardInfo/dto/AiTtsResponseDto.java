package com.potato.balbambalbam.main.cardInfo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
public class AiTtsResponseDto {
    @NotBlank
    private String correctAudio;
}
