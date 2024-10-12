package com.potato.balbambalbam.home.missedCards.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CategoryNotFoundException;
import com.potato.balbambalbam.home.missedCards.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MissedCardsService {
    private final CardRepository cardRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardBookmarkRepository cardBookmarkRepository;

    public List<CardDto> getCards(Long userId){
        List<CardDto> cardDtoList = createCardDtoListForCategory(userId);

        return cardDtoList;
    }

    protected List<CardDto> createCardDtoListForCategory(Long userId){
        List<CardScore> missedCardsByUserId = cardScoreRepository.findByUserId(userId);
        List<CardDto> cardDtoList = new ArrayList<>();

        for(CardScore cardScore : missedCardsByUserId) {
            cardDtoList.add(convertCardToDto(cardScore));
        }

        return cardDtoList;
    }


    protected CardDto convertCardToDto(CardScore cardScore){
        CardDto cardDto = new CardDto();

        Long userId = cardScore.getUserId();
        Long cardId = cardScore.getId();
        Card card = cardRepository.findByCardId(cardId).get();
        cardDto.setId(cardId);
        cardDto.setText(card.getText());

        cardDto.setCardScore(cardScore.getHighestScore());  //사용자 점수가 없으면 0점
        cardDto.setWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId));
        cardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId));
        cardDto.setEngTranslation(card.getCardTranslation());
        cardDto.setEngPronunciation(card.getCardPronunciation());

        return cardDto;
    }
}
