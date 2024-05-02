package com.potato.balbambalbam.weaksoundtest.controller;

import com.potato.balbambalbam.data.entity.UserWeakSound;
import com.potato.balbambalbam.data.repository.UserWeakSoundRepository;
import com.potato.balbambalbam.weaksoundtest.service.PhonemeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
public class PhonemeController {

    private final PhonemeService phonemeService;
    private final UserWeakSoundRepository userWeakSoundRepository;

    public PhonemeController (PhonemeService phonemeService, UserWeakSoundRepository userWeakSoundRepository){
        this.phonemeService = phonemeService;
        this.userWeakSoundRepository = userWeakSoundRepository;
    }

    @PostMapping("/test/finalize")
    public ResponseEntity<?> finalizeAnalysis(@RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("userId 헤더가 필요합니다."); //401
        }

        Map<Long, Integer> topPhonemes = null;

        try {
            topPhonemes = phonemeService.getTopPhonemes(userId);

            if (topPhonemes != null && !topPhonemes.isEmpty()) {
                System.out.println(userId + ":");
                topPhonemes.forEach((phonemeId, count) -> {
                    System.out.println("Phoneme ID : " + phonemeId + ", Count : " + count);
                    UserWeakSound userWeakSound = new UserWeakSound(userId, phonemeId);
                    userWeakSoundRepository.save(userWeakSound);
                });
            } else {
                System.out.println("user id " + userId + " 의 취약음 테스트 결과가 없습니다.");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("취약음 테스트 결과가 없습니다."); //404
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); //500
        } finally {
            phonemeService.clearTemporaryData(userId);
            System.out.println("user id " + userId + " 의 임시 저장소를 삭제했습니다.");
        }
        return ResponseEntity.ok(topPhonemes); //200
    }

    @GetMapping("/test/phonemes")
    public ResponseEntity<?> getWeakPhonemesByUserId(@RequestHeader(value = "userId", required = false) Long userId) {
        if (userId == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("userId 헤더가 필요합니다."); //401
        }

        try {
            List<UserWeakSound> weakPhonemes = userWeakSoundRepository.findAllByUserId(userId);
            if (weakPhonemes == null || weakPhonemes.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("취약음이 없습니다."); //404
            }
            return ResponseEntity.ok(weakPhonemes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류가 발생했습니다."); //500
        }
    }
}