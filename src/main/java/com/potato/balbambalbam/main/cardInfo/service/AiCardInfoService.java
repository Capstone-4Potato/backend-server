package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.main.cardInfo.dto.VoiceRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client

/**
 * 인공지능서버와 통신하여 생성된 음성을 받아옴
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AiCardInfoService {

    public void getTtsVoice(VoiceRequestDto voiceRequestDto) {

    }
}
