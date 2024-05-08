package com.potato.balbambalbam.main.cardFeedback.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Phoneme;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardScoreRepository;
import com.potato.balbambalbam.data.repository.PhonemeRepository;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import com.potato.balbambalbam.exception.CardNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CardFeedbackService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final AiCardFeedbackService aiCardFeedbackService;
    private final PhonemeRepository phonemeRepository;
    private final UpdatePhonemeService updatePhonemeService;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long userId, Long cardId) throws JsonProcessingException {
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId);

        //app 피드백 생성
        updateScoreIfLarger(userId, cardId, aiFeedbackResponseDto.getUserAccuracy());

        //학습카드 추천
        Long categoryId = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getCategoryId();
        Map<Long, String> recommendCards = new HashMap<>();

        if(categoryId >= 15 && categoryId <= 31){
            recommendCards = getRecommendCards(aiFeedbackResponseDto.getRecommendedPronunciations(), aiFeedbackResponseDto.getRecommendedLastPronunciations());
        }

        return setUserFeedbackResponseDto(aiFeedbackResponseDto, recommendCards);
    }

    protected AiFeedbackResponseDto getAiFeedbackResponseDto (UserFeedbackRequestDto aiFeedbackRequest, Long cardId) throws JsonProcessingException {
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(aiFeedbackRequest, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto (UserFeedbackRequestDto userFeedbackRequestDto, Long cardId){
        String pronunciation = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }

    protected void updateScoreIfLarger(Long userId, Long cardId, Integer userScore){
         Optional<CardScore> optionalCardScore = cardScoreRepository.findByCardIdAndUserId(cardId, userId);

        if(optionalCardScore.isPresent()){
            CardScore cardScore = optionalCardScore.get();
            if(cardScore.getHighestScore() < userScore){
                cardScore.setHighestScore(userScore);
                cardScoreRepository.save(cardScore);
            }
        }else{
            cardScoreRepository.save(new CardScore(userScore, userId, cardId));
        }
    }



    protected Map<Long, String> getRecommendCards(List<String> recommendPhonemes, List<String> recommendLastPhonemes){
        Map<Long, String> recommendCards = new HashMap<>();
        //틀린거 4개이상 => 필요없음 걍 다시 도전해보세요!
        if(recommendPhonemes.size() + recommendLastPhonemes.size() > 3){
            recommendCards.put(-1L, null);
            return recommendCards;
        }

        //틀린 음소가 3개 이하일 경우
        recommendPhonemes.forEach(phoneme -> {
            Phoneme foundPhoneme = phonemeRepository.findPhonemeByTextOrderById(phoneme).get(0);
            List<Long> phonemesList = new ArrayList<>();
            String hangul = "";
            if(foundPhoneme.getType() == 0){    //초성
                hangul = updatePhonemeService.createHangul(foundPhoneme.getText(), "ㅏ");

            }else if(foundPhoneme.getType() == 1){  //중성
                hangul = updatePhonemeService.createHangul("ㅇ", foundPhoneme.getText());
            }
            Card foundCard = cardRepository.findByTextOrderById(hangul).getFirst();
            recommendCards.put(foundCard.getId(), hangul);
        });

        //종성
        recommendLastPhonemes.forEach(phoneme -> {
            Long categoryId = getCategoryId(phoneme);
            Card recommendCard = cardRepository.findAllByCategoryId(categoryId).get(0);
            recommendCards.put(recommendCard.getId(), recommendCard.getText());
        });

        return recommendCards;
    }

    //종성 카테고리 아이디 찾기
    protected Long getCategoryId(String phoneme){
        if(phoneme.equals("ㄱ")){
            return 25L;
        }else if(phoneme.equals("ㄴ")){
            return 26L;
        }else if(phoneme.equals("ㄷ")){
            return 27L;
        }else if(phoneme.equals("ㄹ")){
            return 28L;
        }else if(phoneme.equals("ㅁ")){
            return 29L;
        }else if(phoneme.equals("ㅂ")){
            return 30L;
        }else if(phoneme.equals("ㅇ")){
            return 31L;
        }

        return null;
    }

    protected UserFeedbackResponseDto setUserFeedbackResponseDto(AiFeedbackResponseDto aiFeedback, Map<Long, String> recommendCards){
        //사용자 오디오 데이터 생성
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserText(), aiFeedback.getUserMistakenIndexes());

        //waveform 데이터 생성
        UserFeedbackResponseDto.Waveform waveform = new UserFeedbackResponseDto.Waveform(aiFeedback.getUserWaveform(), aiFeedback.getUserAudioDuration(), aiFeedback.getCorrectWaveform(), aiFeedback.getCorrectAudioDuration());

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setUserScore(aiFeedback.getUserAccuracy());
        userFeedbackResponseDto.setRecommendCard(recommendCards);
        userFeedbackResponseDto.setWaveform(waveform);

        return userFeedbackResponseDto;
    }

}
