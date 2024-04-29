package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.customCard.dto.AiPronunciationRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public String getPronunciation(String text) {

        AiPronunciationRequestDto aiPronunciationRequestDto = new AiPronunciationRequestDto(text);

        String pronunciaiton = webClient.post()
                .uri(MyConstant.AI_URL + "/ai/pronunciation")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiPronunciationRequestDto), AiPronunciationRequestDto.class)
                .retrieve()//요청
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .onErrorReturn("TimeoutException")    //에러 대신 TimeoutException return
                .block();

        return pronunciaiton;
    }
}