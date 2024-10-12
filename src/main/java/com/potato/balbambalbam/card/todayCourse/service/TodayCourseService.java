package com.potato.balbambalbam.card.todayCourse.service;

import com.potato.balbambalbam.home.learningCourse.dto.ResponseCardDto;
import com.potato.balbambalbam.card.todayCourse.dto.CourseRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class TodayCourseService {
    public List<ResponseCardDto> getCardList(CourseRequestDto courseRequestDto) {

        Integer courseCnt = courseRequestDto.getCourseCnt();

        return null;
    }
}
