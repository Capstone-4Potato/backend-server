package com.potato.balbambalbam.main.cardFeedback.service;

import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;

@Service
public class AiCardFeedbackService {
    public static final String TEMPORARY_URI = "/아직안정해짐";
    WebClient webClient = WebClient.builder().build();
    public AiFeedbackResponseDto postAiFeedback(AiFeedbackRequestDto aiFeedbackRequestDto) {
        AiFeedbackResponseDto aiFeedbackResponseDto = webClient.post()
                .uri(TEMPORARY_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiFeedbackRequestDto), AiFeedbackRequestDto.class)
                .retrieve()//요청
                .bodyToMono(AiFeedbackResponseDto.class)
                .retryWhen(Retry.backoff(1, Duration.ofSeconds(10)))    //10초 간격으로 1번 재시도 요청
                .timeout(Duration.ofSeconds(10)) //20초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return aiFeedbackResponseDto;
    }
}
