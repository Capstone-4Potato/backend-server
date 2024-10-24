package com.potato.balbambalbam.data.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity(name = "user_attendance")
@IdClass(UserAttendanceId.class)
@Getter
@Setter
public class UserAttendance {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "is_present", nullable = false)
    private Boolean isPresent;

    public UserAttendance() {
    }
}
