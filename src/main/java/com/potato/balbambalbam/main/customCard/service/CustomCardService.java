package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.exception.CardGenerationFailException;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class CustomCardService {

    private final CustomCardRepository customCardRepository;
    private final UserRepository userRepository;
    private final AiPronunciationService aiPronunciationService;

    public Long createCustomCardIfPossible (String text, Long userId) throws CardGenerationFailException {
        if(!canGenerateSentence(userId, text)){
            throw new CardGenerationFailException("카드를 생성할 수 없습니다");
        }

        return createCustomCard(text, userId).getId();
    }

    public boolean deleteCustomCard(Long cardId){
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));

        customCardRepository.delete(customCard);

        if(customCardRepository.findById(cardId).isPresent()){
            return false;
        }

        return true;
    }

    protected boolean canGenerateSentence(Long userId, String text){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다"));

        //기존 커스텀 문장 10개 이상이면 생성 불가
        if(customCardRepository.findAllByUserId(userId).size() >= 10){
            return false;
        }
        //문장에 영어가 있으면 생성 불가
        if(!Pattern.matches("^[ㄱ-ㅎ가-힣]*$", text)){
            return false;
        }

        //문장이 35자 이상이면 생성 불가
        if(text.length() >= 35){
            return false;
        }

        return true;
    }

    protected CustomCard createCustomCard (String text, Long userId){
        CustomCard customCard = new CustomCard();
        customCard.setText(text);
        String pronunciation = aiPronunciationService.getPronunciation(text);
        customCard.setPronunciation(pronunciation);
        customCard.setUserId(userId);

        return customCardRepository.save(customCard);
    }


}
