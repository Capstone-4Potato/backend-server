package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.main.cardInfo.dto.VoiceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * 인공지능서버와 통신하여 생성된 음성을 받아옴
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiCardInfoService {
    public static final String TEMPORARY_URI = "/아직안정해짐";
    WebClient webClient = WebClient.builder().build();
    public String getTtsVoice(VoiceRequestDto voiceRequestDto) {
        String wavFile = webClient.post()
                .uri(TEMPORARY_URI)
                .contentType(MediaType.APPLICATION_JSON)
                .body(voiceRequestDto, VoiceRequestDto.class)
                .retrieve()//요청
                .bodyToMono(String.class)
                .block();

        return wavFile;
    }
}
