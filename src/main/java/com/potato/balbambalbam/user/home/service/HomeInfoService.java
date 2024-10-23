package com.potato.balbambalbam.user.home.service;

import com.potato.balbambalbam.data.entity.UserLevel;
import com.potato.balbambalbam.data.repository.LevelRepository;
import com.potato.balbambalbam.data.repository.UserAttendanceRepository;
import com.potato.balbambalbam.data.repository.UserLevelRepository;
import com.potato.balbambalbam.user.home.dto.HomeInfoDto;
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

    public HomeInfoDto getHomeInfo(Long userId) {
        HomeInfoDto homeInfoDto = new HomeInfoDto();



        return homeInfoDto;
    }

    // 사용자 레벨 정보
    private void setUserLevelInfo(Long userId, HomeInfoDto homeInfoDto) {
        // 회원가입 시 자동 생성
        UserLevel userLevel = userLevelRepository.findById(userId).orElse(null);
    }
}
