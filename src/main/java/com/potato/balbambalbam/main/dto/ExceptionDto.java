package com.potato.balbambalbam.main.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@Schema(description = "에러 발생 시 응답")
public class ExceptionDto {
    @Schema(description = "에러 이름")
    private String exceptionName;
    @Schema(description = "에러 메세지")
    private String message;
    public ExceptionDto(String exceptionName, String message) {
        this.exceptionName = exceptionName;
        this.message = message;
    }
}
