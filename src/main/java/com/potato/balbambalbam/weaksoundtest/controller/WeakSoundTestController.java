package com.potato.balbambalbam.weaksoundtest.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import com.potato.balbambalbam.weaksoundtest.service.WeakSoundTestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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

    @Autowired
    public WeakSoundTestController(WeakSoundTestService weakSoundTestService){
        this.weakSoundTestService = weakSoundTestService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile
            (@RequestParam("userAudio")MultipartFile userAudio,
             @RequestParam("correctText") String correctText) throws JsonProcessingException {
        if(userAudio.isEmpty() || correctText.isEmpty()){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("클라이언트 에러 발생!");
        }
        try{
            byte[] userAudioBytes = userAudio.getBytes(); // userAudio<wav> -> userAudioBytes
            String userAudioBase64 = Base64.getEncoder().encodeToString(userAudioBytes); // userAudioBytes -> userAudioBase64

            Map<String, Object> dataToSend = new HashMap<>();
            dataToSend.put("userAudio",userAudioBase64);
            dataToSend.put("correctText",correctText);

            String testRequestJson = objectMapper.writeValueAsString(dataToSend); // dataToSend -> testRequestJson<JSON>

            WeakSoundTestDto testResponse = weakSoundTestService.sendToAi(testRequestJson);
            String testResponseJson = objectMapper.writeValueAsString(testResponse); // testResponse -> testResponseJson<JSON>
            return ResponseEntity.ok(testResponseJson);
        }catch (IOException e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 에러 발생!");
        }
    }
}