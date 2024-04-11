package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.entity.User;
import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.cardInfo.dto.VoiceRequestDto;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import com.potato.balbambalbam.main.repository.CardRepository;
import com.potato.balbambalbam.main.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Base64;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AiCardInfoService aiCardInfoService;

    public CardInfoResponseDto getCardInfo(Long cardId, Long userId) {
        //음성 생성
        VoiceRequestDto voiceRequestDto = getUserInfo(userId);
        MultipartFile ttsVoice = aiCardInfoService.getTtsVoice(voiceRequestDto);
        String wavToString = convertWavToString(ttsVoice);

        //카드 정보 생성
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));
        return new CardInfoResponseDto(cardId, card.getText(), card.getPronunciation(), wavToString);
    }

    protected VoiceRequestDto getUserInfo(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Integer gender = user.getGender();

        return new VoiceRequestDto(age, gender);
    }

    protected String convertWavToString(MultipartFile ttsVoice) {

        try{
            byte[] voiceBytes = ttsVoice.getBytes();
            String voiceString = Base64.getEncoder().encodeToString(voiceBytes);

            return voiceString;
        }catch (IOException ex){
            ex.printStackTrace();
        }

        return null;
    }

}
