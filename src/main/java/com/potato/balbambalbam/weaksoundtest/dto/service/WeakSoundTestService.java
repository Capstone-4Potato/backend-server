package com.potato.balbambalbam.weaksoundtest.dto.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.MyConstant;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class WeakSoundTestService {

    private final ObjectMapper objectMapper;
    private final PhonemeService phonemeService;
    WebClient webClient = WebClient.builder().build();

    public WeakSoundTestDto sendToAi(Long userId, Map<String, Object> dataToSend) throws JsonProcessingException {
        String testRequestJson = objectMapper.writeValueAsString(dataToSend); // dataToSend -> testRequestJson <Json>
        String testResponseJson = webClient.post()
                .uri(MyConstant.AI_URL + "/ai/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRequestJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        WeakSoundTestDto weakSoundTestDto = objectMapper.readValue(testResponseJson, WeakSoundTestDto.class);
        phonemeService.storePhonemeData(userId, weakSoundTestDto);
        return weakSoundTestDto;
    }
}