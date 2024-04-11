package com.potato.balbambalbam.main;

import com.potato.balbambalbam.main.cardList.dto.ExceptionDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ExceptionDto> runtimeExceptionHandler(RuntimeException ex){
        ExceptionDto exceptionDto = new ExceptionDto("IllegalArgumentException", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exceptionDto);
    }
}
