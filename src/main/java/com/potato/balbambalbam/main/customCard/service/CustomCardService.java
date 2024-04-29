package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.exception.CardCapacityExceededException;
import com.potato.balbambalbam.main.exception.CardNotFoundException;
import com.potato.balbambalbam.main.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomCardService {

    private final CustomCardRepository customCardRepository;
    private final UserRepository userRepository;
    private final AiPronunciationService aiPronunciationService;

    public Long createCustomCardIfPossible (String text, Long userId) throws CardCapacityExceededException {
        if(!canGenerateSentence(userId)){
            throw new CardCapacityExceededException("카드를 더 이상 생성할 수 없습니다");
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

    protected boolean canGenerateSentence(Long userId){
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다"));

        if(customCardRepository.findAllByUserId(userId).size() >= 10){
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
