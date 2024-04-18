package com.potato.balbambalbam.main.cardFeedback.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class AiFeedbackRequestDto {
    @NotBlank
    public String userAudio;
    @NotBlank
    public String correctAudio;
    @NotBlank
    @Size(max = 35)
    public String pronunciation;

}
