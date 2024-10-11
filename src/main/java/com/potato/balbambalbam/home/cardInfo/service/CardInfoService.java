package com.potato.balbambalbam.home.cardInfo.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardVoice;
import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardVoiceRepository;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import com.potato.balbambalbam.exception.VoiceNotFoundException;
import com.potato.balbambalbam.home.cardInfo.dto.AiTtsRequestDto;
import com.potato.balbambalbam.home.cardInfo.dto.AiTtsResponseDto;
import com.potato.balbambalbam.home.cardInfo.dto.CardInfoResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardInfoService {

    private final UserRepository userRepository;
    private final CardRepository cardRepository;
    private final AiTtsService aiTtsService;
    private final CustomCardRepository customCardRepository;
    private final CardVoiceRepository cardVoiceRepository;

    public CardInfoResponseDto getCardInfo(Long userId, Long cardId) {
        AiTtsRequestDto aiTtsRequestDto = getAiTtsRequestDto(userId, getCardText(cardId));
        return getCardVoice(cardId, aiTtsRequestDto);
    }

    public CardInfoResponseDto getCustomCardInfo(Long userId, Long cardId) {
        return createCardVoice(getAiTtsRequestDto(userId, getCustomCardText(cardId, userId)));

    }

    /**
     * 일반 카드 text 추출
     */
    protected String getCardText(Long cardId){
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));
        return card.getText();
    }

    /**
     * 커스텀 카드 text 추출
     */
    protected String getCustomCardText (Long cardId, Long userId){
        CustomCard card = customCardRepository.findCustomCardByIdAndUserId(cardId, userId).orElseThrow(() -> new CardNotFoundException("잘못된 URL 요청입니다"));
        return card.getText();
    }

    /**
     * ai 서버로 부터 tts 받아서 dto 생성
     */
    protected AiTtsRequestDto getAiTtsRequestDto(Long userId, String text){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("사용자가 존재하지 않습니다"));
        Integer age = user.getAge();
        Byte gender = user.getGender();

        return new AiTtsRequestDto(age, gender, text);
    }

    /**
     * 프론트로 전달해줄 customCard용 cardInfoResponseDto 생성
     */
    protected CardInfoResponseDto createCardVoice (AiTtsRequestDto aiTtsRequestDto) {
        AiTtsResponseDto aiTtsResponseDto = aiTtsService.getTtsVoice(aiTtsRequestDto);

        String correctAudio = aiTtsResponseDto.getCorrectAudio();

        return new CardInfoResponseDto(correctAudio);
    }

    /**
     * 프론트로 전달해줄 일반 Card용 cardInfoResponseDto 생성
     */
    protected CardInfoResponseDto getCardVoice (Long cardId, AiTtsRequestDto aiTtsRequestDto) {
        Byte gender = aiTtsRequestDto.getGender();
        Integer age = aiTtsRequestDto.getAge();

        CardVoice cardVoice = cardVoiceRepository.findById(cardId).orElseThrow(() -> new VoiceNotFoundException("TTS 음성이 존재하지 않습니다"));

        String voice = null;
        switch (gender) {
            // 남자
            case (0): {
                if (age <= 14) {// 아이
                    voice = cardVoice.getChildMale();
                } else if(age <= 40) { // 청년
                    voice = cardVoice.getAdultMale();
                } else { //중장년
                    voice = cardVoice.getElderlyMale();
                }
                break;
            }
            // 여자
            case (1) : {
                if(age <= 14) { // 아이
                    voice = cardVoice.getChildFemale();
                } else if(age <= 40) { // 청년
                    voice = cardVoice.getAdultFemale();
                } else { // 중장년
                    voice = cardVoice.getElderlyFemale();
                }
                break;
            }
        }

        return new CardInfoResponseDto(voice);
    }

}
