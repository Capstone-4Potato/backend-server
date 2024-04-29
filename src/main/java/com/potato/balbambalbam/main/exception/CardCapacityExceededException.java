package com.potato.balbambalbam.main.exception;

import org.apache.coyote.BadRequestException;

public class CardCapacityExceededException extends BadRequestException {
    public CardCapacityExceededException(String s) {
        super(s);
    }

}
