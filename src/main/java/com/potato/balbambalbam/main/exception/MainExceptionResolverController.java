package com.potato.balbambalbam.main.exception;

import com.potato.balbambalbam.main.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController {

    @ExceptionHandler({AiConnectionException.class, UserNotFoundException.class, CategoryNotFoundException.class, CardNotFoundException.class})
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex){
        return exceptionHandler(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ExceptionDto> badRequestExceptionHandler (Exception ex){
        return exceptionHandler(ex, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({TimeoutException.class})
    public ResponseEntity<ExceptionDto> timeoutExceptionHandler(Exception ex){
        return exceptionHandler(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * 에러 메서드 처리
     * @param ex
     * @param httpStatus
     * @return
     */
    protected ResponseEntity<ExceptionDto> exceptionHandler(Exception ex, HttpStatus httpStatus){
        String className = extractClassName(ex.getClass().toString());
        String exMessage = ex.getMessage();

        log.info("[ERROR] ["+ className + "]:" + exMessage);

        return ResponseEntity.status(httpStatus).body(new ExceptionDto(className, exMessage));
    }

    protected String extractClassName(String fullClassName){
        String[] classNameParts = fullClassName.split("\\.");
        return classNameParts[classNameParts.length - 1];
    }
}
