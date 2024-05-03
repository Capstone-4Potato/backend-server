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

    private final UserWeakSoundRepository userWeakSoundRepository;

    public PhonemeController (UserWeakSoundRepository userWeakSoundRepository){
        this.userWeakSoundRepository = userWeakSoundRepository;
    }

    @GetMapping("/phonemes")
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