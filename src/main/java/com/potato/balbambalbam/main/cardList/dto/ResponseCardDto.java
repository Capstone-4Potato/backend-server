package com.potato.balbambalbam.main.cardList.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
@Schema(description = "카드리스트 각 카드 정보 Response")
public class ResponseCardDto {

    private Long id;
    private String text;
    private String pronunciation;
    private boolean isBookmark;
    private boolean isWeakCard;
    private int cardScore;

}
