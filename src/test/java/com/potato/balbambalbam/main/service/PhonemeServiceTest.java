package com.potato.balbambalbam.main.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PhonemeServiceTest {

    @Autowired
    public PhonemeService phonemeService;

    @Test
    public void dividePronunciationToPhoneme() {
        String text = "아닌데";
        List<Long> phonemeIds = phonemeService.convertTextToPhonemeIds(text);

    }
}