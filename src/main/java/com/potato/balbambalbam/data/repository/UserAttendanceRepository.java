package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserAttendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    @Query("SELECT ua FROM user_attendance ua " + "WHERE ua.userId = :userId " + "AND ua.attendanceDate BETWEEN " +
            "FUNCTION('DATE_SUB', CURRENT_DATE, FUNCTION('DAYOFWEEK', CURRENT_DATE) - 2) "
            + "AND FUNCTION('DATE_ADD', CURRENT_DATE, 8 - FUNCTION('DAYOFWEEK', CURRENT_DATE)) "
            + "ORDER BY ua.attendanceDate")
    List<UserAttendance> findWeeklyAttendance(@Param("userId") Long userId);
}