package com.potato.balbambalbam.cardInsert;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardVoiceRepository;
import com.potato.balbambalbam.main.cardList.service.UpdateEngPronunciationService;
import com.potato.balbambalbam.main.cardList.service.UpdateEngTranslationService;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import com.potato.balbambalbam.tts.UpdateAllTtsService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CardInsertService {
    private final CardRepository cardRepository;
    private final CardVoiceRepository cardVoiceRepository;
    private final UpdatePhonemeService updatePhonemeService;
    private final UpdateEngPronunciationService updateEngPronunciationService;
    private final UpdateEngTranslationService updateEngTranslationService;
    private final UpdateAllTtsService updateAllTtsService;

    public int updateCardRecordList() {
        List<Card> cardList = cardRepository.findAll();

        cardList.forEach(card -> {
            if(isNeedUpdate(card)){
                updateCardRecord(card);
                cardRepository.save(card);
                log.info("{} update record success", card);
            }
        });

        return cardList.size();
    }
    @Transactional
    protected void updateCardRecord(Card card) {
        updatePhonemeService.updateCardPhonemeColumn(card);
        updateEngPronunciationService.updateEngPronunciation(card);
        updateEngTranslationService.updateEngTranslation(card);
        //updateAllTtsService.updateCardVoice(card);
    }

    protected boolean isNeedUpdate(Card card){
        if(card.getCategoryId() > 31 && (card.getCardTranslation() != null || card.getPhonemesMap() != null || card.getCardPronunciation() != null || cardVoiceRepository.findById(card.getCardId()).isPresent())) {
            return false;
        }
        if(card.getCardTranslation() == null || card.getPhonemesMap() == null || card.getCardPronunciation() == null || !cardVoiceRepository.findById(card.getCardId()).isPresent()){
            return true;
        }
        return false;
    }
}