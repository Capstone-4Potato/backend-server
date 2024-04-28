package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.MyConstant;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
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
    public String getTtsVoice(AiTtsRequestDto aiTtsRequestDto) {
        String wavFile = webClient.post()
                .uri(MyConstant.AI_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(aiTtsRequestDto), AiTtsRequestDto.class)
                .retrieve()//요청
                .bodyToMono(String.class)
                .timeout(Duration.ofSeconds(2)) //2초 안에 응답 오지 않으면 TimeoutException 발생
                .onErrorReturn("TimeoutException")    //에러 대신 TimeoutException return
                .block();

        return wavFile;
    }
}
