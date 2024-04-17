package com.potato.balbambalbam.main.cardFeedback.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@ToString
public class UserFeedbackRequestDto {
    @NotBlank
    private String userAudio;
    @NotBlank
    private String correctAudio;

}
