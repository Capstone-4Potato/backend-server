package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.main.customCard.service.AiPronunciationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateEngPronunciationService {
    private final CardRepository cardRepository;
    private final AiPronunciationService aiPronunciationService;

    public void updateEngPronunciation(Card card) {
        String text = card.getText();
        card.setEngPronunciation(aiPronunciationService.getEngPronunciation(text).getEngPronunciation());
        cardRepository.save(card);
    }
}
