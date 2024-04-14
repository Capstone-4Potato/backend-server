package com.potato.balbambalbam.main;

import com.potato.balbambalbam.main.cardInfo.exception.AiConnectionException;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.main.cardList.dto.ExceptionDto;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import com.potato.balbambalbam.main.cardList.exception.CategoryNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController {

    @ExceptionHandler({AiConnectionException.class, UserNotFoundException.class, CategoryNotFoundException.class, CardNotFoundException.class})
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex){
        return exceptionHandler(ex, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class})
    public ResponseEntity<ExceptionDto> badRequestExceptionHandler (IllegalArgumentException ex){
        return exceptionHandler(ex, HttpStatus.BAD_REQUEST);
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
