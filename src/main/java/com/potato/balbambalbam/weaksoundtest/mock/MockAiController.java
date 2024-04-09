package com.potato.balbambalbam.weaksoundtest.mock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MockAiController {

    @Autowired
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/ai/test")
    public ResponseEntity<String> aiTest(@RequestBody String json) {
        System.out.println("weakSoundTestRequest = " + json);

        Map<String, Object> responseMap = new HashMap<>();

        // 조건에 따라 비어 있거나 내용이 있는 Map 반환
        responseMap.put("userWeakPhoneme", createSampleUserWeakPhoneme());
        responseMap.put("userWeakPhonemeLast", createSampleUserWeakPhonemeLast());

        try {
            String responseJson = objectMapper.writeValueAsString(responseMap);
            System.out.println("weakSoundTestResponse = " + responseJson);
            return ResponseEntity.ok(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"JSON 처리 중 오류 발생\"}");
        }
    }

    private Map<String, Integer> createSampleUserWeakPhoneme() {
        // 데이터가 필요한 경우, 아래 주석을 해제하고 데이터를 추가
        Map<String, Integer> userWeakPhoneme = new HashMap<>();
        userWeakPhoneme.put("ㅏ", 2); // 예시 데이터
        userWeakPhoneme.put("ㅃ", 1); // 예시 데이터
        // 데이터가 없을 경우, 비어 있는 Map 반환
        return userWeakPhoneme;
    }

    private Map<String, Integer> createSampleUserWeakPhonemeLast() {
        // 데이터가 필요한 경우, 아래 주석을 해제하고 데이터를 추가
        Map<String, Integer> userWeakPhonemeLast = new HashMap<>();
        // userWeakPhonemeLast.put("ㄴ", 1); // 예시 데이터

        // 데이터가 없을 경우, 비어 있는 Map 반환
        return userWeakPhonemeLast;
    }
}
