package com.potato.balbambalbam.main;

import com.potato.balbambalbam.main.exception.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController extends ResponseEntityExceptionHandler {
    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        String className = extractClassName(ex.getClass().toString());

        return ResponseEntity.status(statusCode).body("[ERROR] : " + className + "\n message" + ex.getMessage());

    }

    //    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<ExceptionDto> validatedExceptionHandler(MethodArgumentNotValidException ex){
//        String className = extractClassName(ex.getClass().toString());
//        String defaultMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
//
//        log.info("[ERROR] ["+ className + "]:" + defaultMessage);
//
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ExceptionDto(className, defaultMessage));
//    }
//    @ExceptionHandler({AiConnectionException.class, UserNotFoundException.class, CategoryNotFoundException.class, CardNotFoundException.class})
//    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex){
//        return exceptionHandler(ex, HttpStatus.NOT_FOUND);
//    }
//
//    @ExceptionHandler({IllegalArgumentException.class, RuntimeException.class, HttpMessageNotReadableException.class})
//    public ResponseEntity<ExceptionDto> badRequestExceptionHandler (Exception ex){
//        return exceptionHandler(ex, HttpStatus.BAD_REQUEST);
//    }
//
//    @ExceptionHandler({TimeoutException.class, CardDeleteException.class})
//    public ResponseEntity<ExceptionDto> timeoutExceptionHandler(Exception ex){
//        return exceptionHandler(ex, HttpStatus.INTERNAL_SERVER_ERROR);
//    }
//
//    /**
//     * 에러 메서드 처리
//     * @param ex
//     * @param httpStatus
//     * @return
//     */
    protected ResponseEntity<ExceptionDto> exceptionHandler(Exception ex, HttpStatusCode httpStatus){
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
