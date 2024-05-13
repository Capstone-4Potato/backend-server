package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.main.customCard.dto.AiPronunciationRequestDto;
import com.potato.balbambalbam.main.customCard.dto.AiPronunciationResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;
@Slf4j
@RequiredArgsConstructor
@Service
public class AiPronunciationService {
    WebClient webClient = WebClient.builder().build();
    @Value("${ai.service.url}")
    private String AI_URL;
    public AiPronunciationResponseDto getPronunciation(String text) {

        AiPronunciationRequestDto aiPronunciationRequestDto = new AiPronunciationRequestDto(text);

        AiPronunciationResponseDto responseDto = webClient.post()
                .uri(AI_URL + "/ai/pronunciation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiPronunciationRequestDto), AiPronunciationRequestDto.class)
                .retrieve()//요청
                .bodyToMono(AiPronunciationResponseDto.class)
                .timeout(Duration.ofSeconds(2)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return responseDto;
    }
}