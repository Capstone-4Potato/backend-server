package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CardBookmarkRepository;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsResponseDto;
import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.exception.UserNotFoundException;
import com.potato.balbambalbam.main.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final AiTtsService aiTtsService;

    public CardInfoResponseDto getCardInfo(Long userId, Long cardId) {
        //음성 생성
        AiTtsRequestDto aiTtsRequestDto = getAiTtsRequestDto(userId, cardId);
        AiTtsResponseDto aiTtsResponseDto = aiTtsService.getTtsVoice(aiTtsRequestDto);

        String correctAudio = aiTtsResponseDto.getCorrectAudio();

        return new CardInfoResponseDto(correctAudio);
    }

    protected AiTtsRequestDto getAiTtsRequestDto(Long userId, Long cardId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Byte gender = user.getGender();

        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));
        String text = card.getText();

        return new AiTtsRequestDto(age, gender, text);
    }

}
