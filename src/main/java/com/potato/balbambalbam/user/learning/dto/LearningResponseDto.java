package com.potato.balbambalbam.user.learning.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "사용자 학습 진척도 Response")
public class LearningResponseDto {
    private double syllableProgress;
    private double wordProgress;
    private double sentenceProgress;
}
