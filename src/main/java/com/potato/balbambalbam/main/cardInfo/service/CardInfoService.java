package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CardBookmarkRepository;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.cardInfo.exception.AiConnectionException;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
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
    private final AiCardInfoService aiCardInfoService;

    public CardInfoResponseDto getCardInfo(Long cardId, Long userId) {
        //음성 생성
        AiTtsRequestDto aiTtsRequestDto = getUserInfo(userId);
        String wavToString = aiCardInfoService.getTtsVoice(aiTtsRequestDto);
        if(wavToString.equals("TimeoutException")){
            throw new AiConnectionException("음성 생성에 실패하였습니다");
        }
//        String wavToString = "테스트입니다";

        //카드 정보 생성
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));
        boolean isBookmark = cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId);
        return new CardInfoResponseDto(wavToString);
    }

    protected AiTtsRequestDto getUserInfo(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Byte gender = user.getGender();

        return new AiTtsRequestDto(age, gender);
    }

}
