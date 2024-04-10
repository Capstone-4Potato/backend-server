package com.potato.balbambalbam;

import com.potato.balbambalbam.main.service.UpdatePhonemeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * SpringApplication.run() 되기 전에 딱 한번 실행되는 class
 * 1. Card 테이블의 phoneme column update
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MyCommandLineRunner implements CommandLineRunner {

    private final UpdatePhonemeService updatePhonemeService;
    @Override
    public void run(String... args) throws Exception {
        updateCardPhoneme();
    }

    protected void updateCardPhoneme(){
        updatePhonemeService.updateCardPhonemeColumn();
    }
}
