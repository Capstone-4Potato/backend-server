package com.potato.balbambalbam.home.bookmarkCards.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "bookmark 리스트 Response")
public class BookmarkCardResponseDto<T> {
    private T cardList;
    private int count;

    public BookmarkCardResponseDto(T cardList, int count) {
        this.cardList = cardList;
        this.count = count;
    }

}
