package com.potato.balbambalbam.main.cardFeedback.service;

import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.AiFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class CustomCardFeedbackService {
    CustomCardRepository customCardRepository;
    AiCardFeedbackService aiCardFeedbackService;

    public UserFeedbackResponseDto postUserFeedback(UserFeedbackRequestDto userFeedbackRequestDto, Long cardId) {
        //인공지능서버와 통신
        AiFeedbackResponseDto aiFeedbackResponseDto = getAiFeedbackResponseDto(userFeedbackRequestDto, cardId);

        //점수 업데이트
        updateScoreIfLarger(cardId, aiFeedbackResponseDto.getUserAccuracy());

        //학습카드 추천
        Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard = new HashMap<>();
        recommendCard.put(-1L, new UserFeedbackResponseDto.RecommendCardInfo("not word"));

        return setUserFeedbackResponseDto(aiFeedbackResponseDto, recommendCard);
    }

    protected AiFeedbackResponseDto getAiFeedbackResponseDto (UserFeedbackRequestDto aiFeedbackRequest, Long cardId) {
        AiFeedbackRequestDto aiFeedbackRequestDto = createAiFeedbackRequestDto(aiFeedbackRequest, cardId);
        AiFeedbackResponseDto aiFeedbackResponseDto = aiCardFeedbackService.postAiFeedback(aiFeedbackRequestDto);

        return aiFeedbackResponseDto;
    }

    protected AiFeedbackRequestDto createAiFeedbackRequestDto (UserFeedbackRequestDto userFeedbackRequestDto, Long cardId){
        String pronunciation = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다")).getText();
        AiFeedbackRequestDto aiFeedbackRequestDto = new AiFeedbackRequestDto();

        aiFeedbackRequestDto.setUserAudio(userFeedbackRequestDto.getUserAudio());
        aiFeedbackRequestDto.setCorrectAudio(userFeedbackRequestDto.getCorrectAudio());
        aiFeedbackRequestDto.setPronunciation(pronunciation);

        return aiFeedbackRequestDto;
    }

    /**
     * 점수 업데이트
     * @param cardId
     * @param userScore
     */

    public void updateScoreIfLarger(Long cardId, Integer userScore){
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));
        if(customCard.getHighestScore() < userScore){
            customCard.setHighestScore(userScore);
            customCardRepository.save(customCard);
        }
    }

    protected UserFeedbackResponseDto setUserFeedbackResponseDto(AiFeedbackResponseDto aiFeedback, Map<Long, UserFeedbackResponseDto.RecommendCardInfo> recommendCard){
        //사용자 오디오 데이터 생성
        UserFeedbackResponseDto.UserAudio userAudio = new UserFeedbackResponseDto.UserAudio(aiFeedback.getUserText(), aiFeedback.getUserMistakenIndexes());
        //waveform 데이터 생성
        UserFeedbackResponseDto.Waveform waveform = new UserFeedbackResponseDto.Waveform(aiFeedback.getUserWaveform(), aiFeedback.getUserAudioDuration(), aiFeedback.getCorrectWaveform(), aiFeedback.getCorrectAudioDuration());

        //객체 생성 및 설정
        UserFeedbackResponseDto userFeedbackResponseDto = new UserFeedbackResponseDto();
        userFeedbackResponseDto.setUserAudio(userAudio);
        userFeedbackResponseDto.setUserScore(aiFeedback.getUserAccuracy());
        userFeedbackResponseDto.setRecommendCard(recommendCard);
        userFeedbackResponseDto.setWaveform(waveform);

        return userFeedbackResponseDto;
    }
}
