package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

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