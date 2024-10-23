package com.potato.balbambalbam.data.repository;

import com.potato.balbambalbam.data.entity.UserAttendance;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserAttendanceRepository extends JpaRepository<UserAttendance, Long> {
    @Query("SELECT ua FROM user_attendance ua " +
            "WHERE ua.userId = :userId " +
            "AND ua.attendanceDate BETWEEN :startDate AND :endDate " +
            "ORDER BY ua.attendanceDate")
    List<UserAttendance> findWeeklyAttendance(@Param("userId") Long userId,
                                              @Param("startDate") LocalDate startDate,
                                              @Param("endDate") LocalDate endDate);
}