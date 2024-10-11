package com.potato.balbambalbam.home.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UpdateEngTranslationService {
    private final CardRepository cardRepository;
    private final AiEngTranslationService aiEngTranslationService;

    public void updateEngTranslation(Card card){
        String text = card.getText();
        card.setEngTranslation(aiEngTranslationService.getEngTranslation(text).getEngTranslation());
        cardRepository.save(card);
    }
}