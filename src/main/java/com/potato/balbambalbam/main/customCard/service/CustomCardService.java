package com.potato.balbambalbam.main.customCard.service;

import com.potato.balbambalbam.data.entity.CustomCard;
import com.potato.balbambalbam.data.entity.User;
import com.potato.balbambalbam.data.repository.CustomCardRepository;
import com.potato.balbambalbam.data.repository.UserRepository;
import com.potato.balbambalbam.main.customCard.dto.CustomCardResponseDto;
import com.potato.balbambalbam.main.exception.CardNotFoundException;
import com.potato.balbambalbam.main.exception.UserNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomCardService {

    private final CustomCardRepository customCardRepository;
    private final UserRepository userRepository;
    private final AiPronunciationService aiPronunciationService;

    public CustomCardResponseDto createCustomCardIfPossible (String text, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new UserNotFoundException("존재하지 않는 회원입니다"));

        CustomCard customCard = createCustomCard(text, userId);

        return createCustomCardResponse(customCard);
    }

    protected CustomCardResponseDto createCustomCardResponse (CustomCard customCard){
        CustomCardResponseDto customCardResponse = new CustomCardResponseDto();
        customCardResponse.setId(customCard.getId());
        customCardResponse.setText(customCard.getText());
        customCardResponse.setPronunciation(customCard.getPronunciation());

        return customCardResponse;
    }

    public boolean deleteCustomCard(Long cardId){
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("카드가 존재하지 않습니다"));

        customCardRepository.delete(customCard);

        if(customCardRepository.findById(cardId).isPresent()){
            return false;
        }

        return true;
    }


    public CustomCard createCustomCard (String text, Long userId){
        CustomCard customCard = new CustomCard();
        customCard.setText(text);
        String pronunciation = aiPronunciationService.getPronunciation(text).getPronunciation();
        customCard.setPronunciation(pronunciation);
        customCard.setUserId(userId);

        return customCardRepository.save(customCard);
    }


}
