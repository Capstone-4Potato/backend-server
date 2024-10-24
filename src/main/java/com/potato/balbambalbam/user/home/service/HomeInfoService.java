package com.potato.balbambalbam.user.home.service;

import com.potato.balbambalbam.data.entity.Level;
import com.potato.balbambalbam.data.entity.UserAttendance;
import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.LevelRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
import jakarta.transaction.Transactional;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeInfoService {
    private final UserLevelRepository userLevelRepository;
    private final LevelRepository levelRepository;
    private final UserAttendanceRepository userAttendanceRepository;

    @Transactional
    public HomeInfoDto getHomeInfo(Long userId) {
        checkTodayAttendance(userId);

        HomeInfoDto homeInfoDto = new HomeInfoDto();

        setUserLevelInfo(userId, homeInfoDto);
        setUserAttendanceInfo(userId, homeInfoDto);
        setDailyWordInfo(userId, homeInfoDto);

        return homeInfoDto;
    }

    private void checkTodayAttendance(Long userId) {
        LocalDate today = LocalDate.now();
        UserAttendance todayAttendance = userAttendanceRepository.findByUserIdAndAttendanceDate(userId, today);

        if (todayAttendance == null) {
            UserAttendance newAttendance = new UserAttendance();
            newAttendance.setUserId(userId);
            newAttendance.setAttendanceDate(today);
            newAttendance.setIsPresent(true);

            userAttendanceRepository.save(newAttendance);
        }
    }

    // 사용자 레벨 정보
    private void setUserLevelInfo(Long userId, HomeInfoDto homeInfoDto) {
        UserLevel userLevel = userLevelRepository.findByUserId(userId);
        Level level = levelRepository.findByLevelId(userLevel.getLevelId());

        homeInfoDto.setUserLevel(level.getLevel());
        homeInfoDto.setLevelExperience((int) level.getLevelExperience());
        homeInfoDto.setUserExperience((int) userLevel.getUserExperience());
    }

    private void setUserAttendanceInfo(Long userId, HomeInfoDto homeInfoDto) {
        LocalDate now = LocalDate.now();
        LocalDate monday = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate sunday = now.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));

        List<UserAttendance> attendances = userAttendanceRepository.findWeeklyAttendance(userId, monday, sunday);

        char[] weekAttendance = new char[7]; // 월~일까지 7일
        Arrays.fill(weekAttendance, 'F');

        for (UserAttendance attendance : attendances) {
            if (attendance.getIsPresent()) {
                int dayOfWeek = attendance.getAttendanceDate().getDayOfWeek().getValue() - 1;
                weekAttendance[dayOfWeek] = 'T';
            }
        }
        homeInfoDto.setWeeklyAttendance(new String(weekAttendance));
    }

    private void setDailyWordInfo(Long userId, HomeInfoDto homeInfoDto) {
        // TODO: 실제 일일 단어 서비스 구현 시 이 부분 수정 필요
        homeInfoDto.setDailyWord("든든해");
        homeInfoDto.setDailyWordMeaning("reliable");
        homeInfoDto.setIsBookmarked(false);
    }

}
