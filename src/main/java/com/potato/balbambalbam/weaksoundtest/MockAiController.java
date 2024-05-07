package com.potato.balbambalbam.weaksoundtest.mock;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@RestController
public class MockAiController {

    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/ai/test")
    public ResponseEntity<String> aiTest(@RequestBody String json) {
        // 받은 JSON 출력
        System.out.println("Received JSON: " + json);

        // 응답을 위한 더미 데이터 생성
        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("userWeakPhoneme", createSampleUserWeakPhoneme());
        responseMap.put("userWeakPhonemeLast", createSampleUserWeakPhonemeLast());

        try {
            // Map을 JSON 문자열로 변환
            String responseJson = objectMapper.writeValueAsString(responseMap);
            // 최종 JSON 응답 출력
            System.out.println("Response JSON: " + responseJson);
            return ResponseEntity.ok(responseJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"JSON 처리 중 오류 발생\"}");
        }
    }

    // 테스트를 위한 더미 데이터 생성 메소드
    private Map<String, Integer> createSampleUserWeakPhoneme() {
        Map<String, Integer> userWeakPhoneme = new HashMap<>();
        userWeakPhoneme.put("ㄱ", 10);
        userWeakPhoneme.put("ㅆ", 5);
        userWeakPhoneme.put("ㄲ", 5);
        userWeakPhoneme.put("", 3);
        return userWeakPhoneme;
    }

    // 테스트를 위한 더미 데이터 생성 메소드
    private Map<String, Integer> createSampleUserWeakPhonemeLast() {
        Map<String, Integer> userWeakPhonemeLast = new HashMap<>();
        userWeakPhonemeLast.put("ㄱ", 2);
        userWeakPhonemeLast.put("ㄹ", 2);
        userWeakPhonemeLast.put("ㄷ", 2);
        return userWeakPhonemeLast;
    }
}