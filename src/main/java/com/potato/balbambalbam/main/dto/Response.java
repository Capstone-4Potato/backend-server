package com.potato.balbambalbam.main.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Response<T> {
    private T data;
    private int count;

    public Response(T data, int count) {
        this.data = data;
        this.count = count;
    }
}
