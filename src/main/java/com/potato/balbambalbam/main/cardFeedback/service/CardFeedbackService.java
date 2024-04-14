package com.potato.balbambalbam.main.cardFeedback.service;

import com.potato.balbambalbam.entity.CardScore;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import com.potato.balbambalbam.main.repository.CardRepository;
import com.potato.balbambalbam.main.repository.CardScoreRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardFeedbackService {

    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final AiCardFeedbackService aiCardFeedbackService;

    public UserFeedbackResponseDto postUserFeedbackForTest(){
        return setUserFeedbackResponseDtoForTest();
    }

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long userId, Long cardId){
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(userFeedbackRequestDto, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);
        Integer highestScore = updateScoreIfLarger(userId, cardId, aiFeedbackResponseDto.getUserAccuracy());
        Long categoryId = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getCategoryId();

        //TODO : 학습카드 추천 구현
        if(categoryId >= 15 && categoryId <= 31){
            List<Integer> recommendCardIds = getRecommendCardId(aiFeedbackResponseDto.getRecommendedPronunciations(), aiFeedbackResponseDto.getRecommendedLastPronunciations());
        }else{
            //음절이나 문장인 경우 null 반환
            List<Character> pronunciations = new ArrayList<>();
            List<Integer> recommendCardIds = new ArrayList<>();
        }

        return null;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto (UserFeedbackRequestDto userFeedbackRequestDto, Long cardId){
        String pronunciation = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getPronunciation();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }

    protected Integer updateScoreIfLarger(Long userId, Long cardId, Integer cardScore){
        Integer highestScore = cardScore;

        Integer userScore = cardScoreRepository.findByCardIdAndUserId(cardId, userId).map(CardScore::getHighestScore).orElse(0);
        if(userScore > cardScore){
            highestScore = userScore;
        }

        cardScoreRepository.save(new CardScore(highestScore, userId, cardId));
        return highestScore;
    }

    protected UserFeedbackResponseDto setUserFeedbackResponseDtoForTest(){
        //사용자 오디오 데이터 생성
        List<Integer> mistakenIndexes = Arrays.asList(1,2);
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio("사용자발음", mistakenIndexes);

        //사용자 점수 데이터 생성
        UserFeedbackResponseDto.UserScore userScore = new UserFeedbackResponseDto.UserScore(20, 80);

        //추천 카드 데이터 생성
        List<Character> pronunciation = Arrays.asList('ㄱ', 'ㄴ');
        List<Long> ids = Arrays.asList(22L, 134L);
        UserFeedbackResponseDto.RecommendCard recommendCard = new UserFeedbackResponseDto.RecommendCard(pronunciation, ids);

        //waveform 데이터 생성
        UserFeedbackResponseDto.Waveform waveform = new UserFeedbackResponseDto.Waveform("userWaveForm", 2.5, "correctWaveform", 3.3);

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setUserScore(userScore);
        userFeedbackResponseDto.setRecommendCard(recommendCard);
        userFeedbackResponseDto.setWaveform(waveform);

        return userFeedbackResponseDto;
    }

    protected List<Integer> getRecommendCardId(List<Character> recommendCards, List<Character> recommendLastCards){
        List<Integer> recommendCardIds = new ArrayList<>();
        //틀린거 4개이상 => 필요없음 걍 다시 도전해보세요!
        if(recommendCards.size() + recommendLastCards.size() > 3){
            recommendCardIds.add(-1);
            return recommendCardIds;
        }

        //틀린 음소가 3개 이하일 경우
        //종성
//        recommendLastCards.forEach(card -> {
//
//        });
//        recommendCards.stream().map(card -> {
//
//        })

        return recommendCardIds;
    }

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

}
