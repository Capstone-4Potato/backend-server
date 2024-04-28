package com.potato.balbambalbam.weaksoundtest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import com.potato.balbambalbam.weaksoundtest.service.WeakSoundTestService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WeakSoundTestController {
    private ObjectMapper objectMapper = new ObjectMapper();
    private final WeakSoundTestService weakSoundTestService;
    private final WeakSoundTestRepository weakSoundTestRepository;

    @PostMapping("/test/{cardId}")
    public ResponseEntity<String> uploadFile
            (@PathVariable("cardId") Long id,
             @RequestHeader("userId") Long userId,
             @RequestParam("userAudio")MultipartFile userAudio) throws JsonProcessingException {
        if(userAudio.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("사용자 음성 파일이 비었습니다.");
        }
        return weakSoundTestRepository.findById(id)
                .map(weakSoundTest -> {
                    try{
                        byte[] userAudioBytes = userAudio.getBytes(); // userAudio<wav> -> userAudioBytes
                        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes); // userAudioBytes -> userAudioBase64

                        Map<String, Object> dataToSend = new HashMap<>();
                        dataToSend.put("userAudio",userAudioBase64);
                        dataToSend.put("correctText",weakSoundTest.getText());

                        WeakSoundTestDto testResponse = weakSoundTestService.sendToAi(userId, dataToSend);
                        String testResponseJson = objectMapper.writeValueAsString(testResponse);
                        return ResponseEntity.ok(testResponseJson);
                    } catch (IOException e){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생!");
                    }
                })
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 id를 가진 테스트 카드가 없습니다."));
    }
}