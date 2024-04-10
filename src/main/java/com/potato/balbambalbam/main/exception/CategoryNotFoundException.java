package com.potato.balbambalbam.main.exception;

public class CategoryNotFoundException extends IllegalArgumentException{
    public CategoryNotFoundException(String message) {
        super(message);
    }
}
