package com.potato.balbambalbam.main.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
class PhonemeServiceTest {

    @Autowired
    public UpdatePhonemeService updatePhonemeService;

    @Test
    public void dividePronunciationToPhoneme() {
        String text = "아닌데";
        List<Long> phonemeIds = updatePhonemeService.convertTextToPhonemeIds(text);

        Assertions.assertThat(phonemeIds).contains(8L, 19L, 2L, 24L, 41L, 3L, 26L);
    }
}