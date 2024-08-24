package com.potato.balbambalbam.learningInfo.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
@Schema(description = "취약음소 테스트 목록 Response")
public class WeakSoundTestResponseDto {
    private Map<String, Integer> userWeakPhoneme; //초성, 중성
    private Map<String, Integer> userWeakPhonemeLast; //종성
}