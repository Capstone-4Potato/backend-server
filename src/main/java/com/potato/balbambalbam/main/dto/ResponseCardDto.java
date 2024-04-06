package com.potato.balbambalbam.main.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@ToString
public class ResponseCardDto {

    private Long id;
    private String text;
    private boolean isBookmark;
    private boolean isWeakCard;
    private int cardScore;

}
