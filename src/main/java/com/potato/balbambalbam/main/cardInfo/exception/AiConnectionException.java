package com.potato.balbambalbam.main.cardInfo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(code = HttpStatus.NOT_FOUND, reason = "통신 가능 최대 시간 초과.")
public class AiConnectionException extends RuntimeException {
    public AiConnectionException(String message) {
        super(message);
    }
}
