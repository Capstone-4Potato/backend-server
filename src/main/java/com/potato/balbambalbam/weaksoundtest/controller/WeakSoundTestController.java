package com.potato.balbambalbam.weaksoundtest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import com.potato.balbambalbam.weaksoundtest.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.weaksoundtest.service.WeakSoundTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@RestController
public class WeakSoundTestController {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();
    private WeakSoundTestService weakSoundTestService;
    private WeakSoundTestRepository weakSoundTestRepository;

    @Autowired
    public WeakSoundTestController(WeakSoundTestService weakSoundTestService,
                                   WeakSoundTestRepository weakSoundTestRepository){
        this.weakSoundTestService = weakSoundTestService;
        this.weakSoundTestRepository = weakSoundTestRepository;
    }

    @PostMapping("/test/{id}")
    public ResponseEntity<String> uploadFile
            (@PathVariable("id") Long id,
             @RequestParam("userAudio")MultipartFile userAudio) throws JsonProcessingException {
        if(userAudio.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("클라이언트 에러 발생!");
        }
        return weakSoundTestRepository.findById(id)
                .map(weakSoundTest -> {
                    try{
                        byte[] userAudioBytes = userAudio.getBytes(); // userAudio<wav> -> userAudioBytes
                        String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes); // userAudioBytes -> userAudioBase64

                        Map<String, Object> dataToSend = new HashMap<>();
                        dataToSend.put("userAudio",userAudioBase64);
                        dataToSend.put("correctText",weakSoundTest.getText());

                        WeakSoundTestDto testResponse = weakSoundTestService.sendToAi(dataToSend);
                        String testResponseJson = objectMapper.writeValueAsString(testResponse);
                        return ResponseEntity.ok(testResponseJson);
                    } catch (IOException e){
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생!");
                    }
                })
                .orElseGet(()-> ResponseEntity.status(HttpStatus.NOT_FOUND).body("해당 ID를 가진 데이터 없음!"));
    }
}