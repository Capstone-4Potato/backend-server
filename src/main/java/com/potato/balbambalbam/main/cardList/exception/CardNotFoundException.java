package com.potato.balbambalbam.main.cardList.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청입니다.")
public class CardNotFoundException extends IllegalArgumentException{
    public CardNotFoundException(String s) {
        super(s);
    }
}
