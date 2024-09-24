package com.potato.balbambalbam.log.repository;

import com.potato.balbambalbam.log.entity.ExceptionInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExceptionInfoRepository extends JpaRepository<ExceptionInfo, Long> {
}
