package com.potato.balbambalbam.weaksoundtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.data.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class WeakSoundTestService {

    private WebClient webClient;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PhonemeService phonemeService;
    @Autowired
    private WeakSoundTestRepository weakSoundTestRepository;

    public WeakSoundTestService(WebClient.Builder webClientBuilder,
                                   @Value("${ai.service.url}") String aiServiceUrl){
        this.webClient = webClientBuilder.baseUrl(aiServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    public WeakSoundTestDto sendToAi(Long userId, Map<String, Object> dataToSend) throws JsonProcessingException {
        String testRequestJson = objectMapper.writeValueAsString(dataToSend); // dataToSend -> testRequestJson <Json>
        String testResponseJson = webClient.post()
                .uri("/ai/test")
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