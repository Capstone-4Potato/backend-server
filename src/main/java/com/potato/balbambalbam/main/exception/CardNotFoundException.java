package com.potato.balbambalbam.main.exception;

public class CardNotFoundException extends IllegalArgumentException{
    public CardNotFoundException(String s) {
        super(s);
    }
}
