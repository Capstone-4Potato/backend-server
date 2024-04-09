package com.potato.balbambalbam.weaksoundtest.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.potato.balbambalbam.weaksoundtest.repository.WeakSoundTestRepository;
import com.potato.balbambalbam.weaksoundtest.dto.WeakSoundTestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WeakSoundTestService {

    private WebClient webClient;

    @Autowired
    private ObjectMapper objectMapper;

    public WeakSoundTestService(WebClient.Builder webClientBuilder,
                                   @Value("${ai.service.url}") String aiServiceUrl){
        this.webClient = webClientBuilder.baseUrl(aiServiceUrl).build();
        this.objectMapper = objectMapper;
    }

    @Autowired
    private WeakSoundTestRepository weakSoundTestRepository;

    public WeakSoundTestDto sendToAi(String testRequestJson) throws JsonProcessingException {
        String testResponseJson = webClient.post()
                .uri("/ai/test")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testRequestJson)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        return objectMapper.readValue(testResponseJson, WeakSoundTestDto.class); // testResponseJson<Json> -> WeakSoundTestDto
    }
}