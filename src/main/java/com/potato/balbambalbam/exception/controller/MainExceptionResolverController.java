package com.potato.balbambalbam.exception.controller;

import com.potato.balbambalbam.exception.*;
import com.potato.balbambalbam.log.ExceptionLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.concurrent.TimeoutException;

@Slf4j
@RestControllerAdvice
public class MainExceptionResolverController extends ResponseEntityExceptionHandler {

    private final ExceptionLogService exceptionLogService;

    @Autowired
    public MainExceptionResolverController(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        exceptionLogService.logException(ex, HttpStatus.valueOf(statusCode.value()), request);
        String className = extractClassName(ex.getClass().toString());
        ExceptionDto exceptionDto = new ExceptionDto(statusCode.value(), className, ex.getMessage());
        return new ResponseEntity<>(exceptionDto, headers, statusCode);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        exceptionLogService.logException(ex, HttpStatus.valueOf(status.value()), request);
        String className = extractClassName(ex.getClass().toString());
        String defaultMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();
        ExceptionDto exceptionDto = new ExceptionDto(status.value(), className, defaultMessage);
        return new ResponseEntity<>(exceptionDto, headers, status);
    }

    @ExceptionHandler({IllegalArgumentException.class, UserNotFoundException.class,
            CategoryNotFoundException.class, CardNotFoundException.class,
            InvalidParameterException.class, ResponseNotFoundException.class})
    public ResponseEntity<ExceptionDto> notFoundExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        exceptionLogService.logException(ex, status, request);
        return exceptionHandler(ex, status);
    }

    @ExceptionHandler({RuntimeException.class, AiConnectionException.class,
            TimeoutException.class, CardDeleteException.class,
            WebClientResponseException.InternalServerError.class,
            AiGenerationFailException.class, VoiceNotFoundException.class})
    public ResponseEntity<ExceptionDto> internalServerErrorExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        exceptionLogService.logException(ex, status, request);
        return exceptionHandler(ex, status);
    }

    @ExceptionHandler({SocialIdChangeException.class, InvalidUserNameException.class,
            ParameterNotFoundException.class})
    public ResponseEntity<ExceptionDto> badRequestExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.BAD_REQUEST;
        exceptionLogService.logException(ex, status, request);
        return exceptionHandler(ex, status);
    }

    @ExceptionHandler({TokenExpiredException.class})
    public ResponseEntity<ExceptionDto> unauthorizedExceptionHandler(Exception ex, WebRequest request) {
        HttpStatus status = HttpStatus.UNAUTHORIZED;
        exceptionLogService.logException(ex, status, request);
        return exceptionHandler(ex, status);
    }

    protected ResponseEntity<ExceptionDto> exceptionHandler(Exception ex, HttpStatusCode httpStatus) {
        String className = extractClassName(ex.getClass().toString());
        String exMessage = ex.getMessage();
        log.error("[ERROR] [{}]: {}", className, exMessage);
        return ResponseEntity.status(httpStatus).body(new ExceptionDto(httpStatus.value(), className, exMessage));
    }

    protected String extractClassName(String fullClassName) {
        String[] classNameParts = fullClassName.split("\\.");
        return classNameParts[classNameParts.length - 1];
    }
}