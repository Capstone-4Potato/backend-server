package com.potato.balbambalbam.tts;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardVoice;
import com.potato.balbambalbam.data.repository.CardRepository;
import com.potato.balbambalbam.data.repository.CardVoiceRepository;
import com.potato.balbambalbam.tts.dto.AiAllTtsResponseDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UpdateAllTtsService {
    private final AiAllTtsService aiAllTtsService;
    private final CardRepository cardRepository;
    private final CardVoiceRepository cardVoiceRepository;

    public void updateCardVoice(Card card) {
        saveSixVoices(card.getId(), aiAllTtsService.getAiTtsResponse(card.getText()));
    }

    @Transactional
    protected void saveSixVoices(Long id, AiAllTtsResponseDto allTtsResponseDto) {

        CardVoice cardvoice = CardVoice.builder()
                .id(id)
                .childMale(allTtsResponseDto.getChild_0())
                .childFemale(allTtsResponseDto.getChild_1())
                .adultMale(allTtsResponseDto.getAdult_0())
                .adultFemale(allTtsResponseDto.getAdult_1())
                .elderlyMale(allTtsResponseDto.getElderly_0())
                .elderlyFemale(allTtsResponseDto.getElderly_1())
                .build();

        cardVoiceRepository.save(cardvoice);
    }
}
