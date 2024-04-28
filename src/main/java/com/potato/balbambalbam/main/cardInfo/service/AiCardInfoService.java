package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.MyConstant;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.Duration;

/**
 * 인공지능서버와 통신하여 생성된 음성을 받아옴
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiCardInfoService {
    WebClient webClient = WebClient.builder().build();
    public AiTtsResponseDto getTtsVoice(AiTtsRequestDto aiTtsRequestDto) {
        AiTtsResponseDto aiTtsResponseDto = webClient.post()
                .uri(MyConstant.AI_URL + "/ai/voice")
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiTtsRequestDto), AiTtsRequestDto.class)
                .retrieve()//요청
                .bodyToMono(AiTtsResponseDto.class)
                .timeout(Duration.ofSeconds(2)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .block();

        return aiTtsResponseDto;
    }
}
