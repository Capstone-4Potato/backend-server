package com.potato.balbambalbam.log;

import com.potato.balbambalbam.log.entity.ExceptionInfo;
import com.potato.balbambalbam.log.entity.ExceptionLog;
import com.potato.balbambalbam.log.repository.ExceptionInfoRepository;
import com.potato.balbambalbam.log.repository.ExceptionLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Service
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
        // ExceptionInfo 저장
        ExceptionInfo exceptionInfo = new ExceptionInfo();
        exceptionInfo.setExceptionName(ex.getClass().getSimpleName());
        exceptionInfo.setExceptionMessage(ex.getMessage());
        exceptionInfo.setExceptionHttpStatus(httpStatus.value());
        exceptionInfo.setExceptionLevel(getExceptionLevel(httpStatus));
        ExceptionInfo savedInfo = exceptionInfoRepository.save(exceptionInfo);

        // ExceptionLog 저장
        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setExceptionInfoId(savedInfo.getExceptionInfoId());
        exceptionLog.setTimestamp(LocalDateTime.now());
        exceptionLog.setClassName(ex.getStackTrace()[0].getClassName());
        exceptionLog.setRequestPath(((ServletWebRequest) request).getRequest().getRequestURI());
        exceptionLogRepository.save(exceptionLog);
    }

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
