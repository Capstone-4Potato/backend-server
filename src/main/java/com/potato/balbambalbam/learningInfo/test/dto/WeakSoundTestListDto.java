package com.potato.balbambalbam.learningInfo.test.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "취약음소 테스트 목록 Response")
public class WeakSoundTestListDto {
    private Long id;
    private String text;
    private String Pronunciation;
    private String engPronunciation;
    private String engTranslation;

    public WeakSoundTestListDto(Long id, String text, String pronunciation, String engPronunciation, String engTranslation) {
    }
}
