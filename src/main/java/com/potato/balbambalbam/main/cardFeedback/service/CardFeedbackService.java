package com.potato.balbambalbam.main.cardFeedback.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.entity.CardScore;
import com.potato.balbambalbam.entity.Phoneme;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import com.potato.balbambalbam.main.repository.CardRepository;
import com.potato.balbambalbam.main.repository.CardScoreRepository;
import com.potato.balbambalbam.main.repository.PhonemeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CardFeedbackService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final AiCardFeedbackService aiCardFeedbackService;
    private final PhonemeRepository phonemeRepository;
    private final UpdatePhonemeService updatePhonemeService;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long userId, Long cardId){
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId);

        //app 피드백 생성
        Integer highestScore = updateScoreIfLarger(userId, cardId, aiFeedbackResponseDto.getUserAccuracy());

        //학습카드 추천
        Long categoryId = cardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getCategoryId();
        Map<Long, String> recommendCards = new HashMap<>();

        if(categoryId >= 15 && categoryId <= 31){
            recommendCards = getRecommendCards(aiFeedbackResponseDto.getRecommendedPronunciations(), aiFeedbackResponseDto.getRecommendedLastPronunciations());
        }

        return setUserFeedbackResponseDto(aiFeedbackResponseDto, highestScore, recommendCards);
    }

    protected AiFeedbackResponseDto getAiFeedbackResponseDto (UserFeedbackRequestDto aiFeedbackRequest, Long cardId){
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(aiFeedbackRequest, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
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

    protected UserFeedbackResponseDto setUserFeedbackResponseDto(AiFeedbackResponseDto aiFeedback,
                                                                 Integer highestScore,
                                                                 Map<Long, String> recommendCards){
        //사용자 오디오 데이터 생성
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserText(), aiFeedback.getUserMistakenIndexes());

        //사용자 점수 데이터 생성
        UserFeedbackResponseDto.UserScore userScore = new UserFeedbackResponseDto.UserScore(aiFeedback.getUserAccuracy(), highestScore);

        //waveform 데이터 생성
        UserFeedbackResponseDto.Waveform waveform = new UserFeedbackResponseDto.Waveform(aiFeedback.getUserWaveform(), aiFeedback.getUserAudioDuration(), aiFeedback.getCorrectWaveform(), aiFeedback.getCorrectAudioDuration());

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setUserScore(userScore);
        userFeedbackResponseDto.setRecommendCard(recommendCards);
        userFeedbackResponseDto.setWaveform(waveform);

        return userFeedbackResponseDto;
    }

}
