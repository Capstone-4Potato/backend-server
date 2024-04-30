package com.potato.balbambalbam.main.cardFeedback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Service
@Slf4j
public class AiCardFeedbackService {
    WebClient webClient = WebClient.builder().build();
    public AiFeedbackResponseDto postAiFeedback(AiFeedbackRequestDto aiFeedbackRequestDto) throws JsonProcessingException {

        log.info("[AI Request Dto] : {}" , aiFeedbackRequestDto.getPronunciation());

        AiFeedbackResponseDto aiFeedbackResponseDto = webClient.post()
                .uri(MyConstant.AI_URL + "/ai/feedback")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiFeedbackRequestDto), AiFeedbackRequestDto.class)
                .retrieve()//요청
                .bodyToMono(AiFeedbackResponseDto.class)
                .timeout(Duration.ofSeconds(10)) //10초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        log.info("[AI Response Dto] : {}" , aiFeedbackResponseDto.getUserText());

        return aiFeedbackResponseDto;
    }
}
