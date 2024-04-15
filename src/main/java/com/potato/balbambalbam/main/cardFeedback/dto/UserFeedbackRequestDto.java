package com.potato.balbambalbam.main.cardFeedback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
public class UserFeedbackRequestDto {
    @NotBlank
    private String userAudio;
    @NotBlank
    private String correctAudio;

}