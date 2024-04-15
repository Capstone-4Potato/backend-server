package com.potato.balbambalbam.main.cardFeedback.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CardFeedbackServiceTest {

    @Autowired
    CardFeedbackService cardFeedbackService;

    @Test
    void getRecommendCardIdTest(){
        //given
        List<String> recommendPhonemes = new ArrayList<>();
        recommendPhonemes.add("ㅆ"); //싸
        recommendPhonemes.add("ㅖ"); //예
        List<String> recommendLastPhonemes = new ArrayList<>();
        recommendLastPhonemes.add("ㄹ"); //반달
        //when

        Map<Long, String> recommendCards = cardFeedbackService.getRecommendCards(recommendPhonemes, recommendLastPhonemes);

        //then
        Assertions.assertThat(recommendCards).containsValues("싸", "예", "반달");

    }
}