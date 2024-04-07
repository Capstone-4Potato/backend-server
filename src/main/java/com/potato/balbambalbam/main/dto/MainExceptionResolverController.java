package com.potato.balbambalbam.main.dto;

import com.potato.balbambalbam.main.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> illegalArgumentExceptionHandler(IllegalArgumentException ex){
        ExceptionDto exceptionDto = new ExceptionDto("IllegalArgumentException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }
}
