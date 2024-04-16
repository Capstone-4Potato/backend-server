package com.potato.balbambalbam.weaksoundtest.controller;

import com.potato.balbambalbam.entity.UserWeakSound;
import com.potato.balbambalbam.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.weaksoundtest.service.PhonemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
public class PhonemeController {

    @Autowired
    private PhonemeService phonemeService;
    @Autowired
    private UserWeakSoundRepository userWeakSoundRepository;

    @PostMapping("/test/finalize")
    public ResponseEntity<Map<Long, Integer>> finalizeAnalysis(@RequestHeader Long userId) {
        Map<Long, Integer> topPhonemes = null;

        try {
            topPhonemes = phonemeService.getTopPhonemes(userId);

            if (topPhonemes != null && !topPhonemes.isEmpty()) {
                System.out.println(userId + ":");
                topPhonemes.forEach((phonemeId, count) -> {
                    System.out.println("Phoneme ID: " + phonemeId + ", Count: " + count);
                    UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
                    userWeakSoundRepository.save(userWeakSound);
                });
            } else {
                System.out.println("user id " + userId + " 의 취얌음 테스트 결과가 없습니다.");
            }
        } catch (Exception e) {
            System.out.println("An error occurred: " + e.getMessage());
            return ResponseEntity.internalServerError().build();
        } finally {
            phonemeService.clearTemporaryData(userId);
        }

        return ResponseEntity.ok(topPhonemes);
    }
}