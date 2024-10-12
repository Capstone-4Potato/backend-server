package com.potato.balbambalbam.user.home.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Schema(description = "홈 기본 정보 Response")
public class HomeInfoDto {

    @Schema(description = "사용자 레벨", example = "3")
    private Integer userLevel;
    @Schema(description = "레벨 총 경험치", example = "30")
    private Integer levelExperience;
    @Schema(description = "사용자 경험치", example = "17")
    private Integer userExperience;
    @Schema(description = "주간 출석 상황", example = "[true, true, false, true, false, false, false]")
    private List<Boolean> weeklyAttendance;
    @Schema(description = "오늘의 추천 단어", example = "든든해")
    private String dailyWord;
    @Schema(description = "오늘의 추천 단어 의미", example = "reliable")
    private String dailyWordMeaning;
    @Schema(description = "오늘의 추천 단어 북마크 여부", example = "false")
    private Boolean isBookmarked;

}
