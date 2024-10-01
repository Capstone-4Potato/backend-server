package com.potato.balbambalbam.log.service;

import com.potato.balbambalbam.log.entity.ExceptionInfo;
import com.potato.balbambalbam.log.entity.ExceptionLog;
import com.potato.balbambalbam.log.repository.ExceptionInfoRepository;
import com.potato.balbambalbam.log.repository.ExceptionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Service
@Transactional
public class ExceptionLogService {

    private final ExceptionInfoRepository exceptionInfoRepository;
    private final ExceptionLogRepository exceptionLogRepository;

    @Autowired
    public ExceptionLogService(ExceptionInfoRepository exceptionInfoRepository,
                               ExceptionLogRepository exceptionLogRepository) {
        this.exceptionInfoRepository = exceptionInfoRepository;
        this.exceptionLogRepository = exceptionLogRepository;
    }

    public void logException(Exception ex, HttpStatus httpStatus, WebRequest request) {
        // 기존 ExceptionInfo 찾기 또는 새로 생성
        ExceptionInfo exceptionInfo = findOrCreateExceptionInfo(ex, httpStatus);

        // ExceptionLog 저장
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExceptionInfoId(exceptionInfo.getExceptionInfoId());
        exceptionLog.setTimestamp(LocalDateTime.now());
        exceptionLog.setClassName(ex.getStackTrace()[0].getClassName());
        exceptionLog.setRequestPath(((ServletWebRequest) request).getRequest().getRequestURI());
        exceptionLogRepository.save(exceptionLog);

    }

    // ExceptionInfo 찾기 또는 생성
    private ExceptionInfo findOrCreateExceptionInfo(Exception ex, HttpStatus httpStatus) {
        String exceptionName = ex.getClass().getSimpleName();
        String exceptionMessage = ex.getMessage();
        int exceptionHttpStatus = httpStatus.value();
        String exceptionLevel = getExceptionLevel(httpStatus);

        return exceptionInfoRepository.findByExceptionNameAndExceptionMessageAndExceptionHttpStatus(
                        exceptionName, exceptionMessage, exceptionHttpStatus)
                .orElseGet(() -> {
                    // 없으면 생성
                    ExceptionInfo newInfo = new ExceptionInfo();
                    newInfo.setExceptionName(exceptionName);
                    newInfo.setExceptionMessage(exceptionMessage);
                    newInfo.setExceptionHttpStatus(exceptionHttpStatus);
                    newInfo.setExceptionLevel(exceptionLevel);
                    return exceptionInfoRepository.save(newInfo);
                });
    }

    // HTTP 상태 코드에 따른 예외 레벨
    private String getExceptionLevel(HttpStatus httpStatus) {
        if (httpStatus.is5xxServerError()) {
            return "ERROR";
        } else if (httpStatus.is4xxClientError()) {
            return "WARN";
        } else {
            return "INFO";
        }
    }
}
