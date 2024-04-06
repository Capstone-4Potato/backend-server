package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.entity.CardScore;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import com.potato.balbambalbam.main.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CardListService {
    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardScoreRepository cardScoreRepository;

    //요청하는 카테고리 찾기
    public Long returnRequestCategory(String category, String subcategory){
        //부모 카테고리 아이디 찾기
        Long parentId = categoryRepository.findByName(category).getId();
        //하위 카테고리 아이디 찾기
        Long requestCategoryId = categoryRepository.findByNameAndParentId(subcategory, parentId).getId();

        return requestCategoryId;
    }

    //카드 리스트 구성 & 반환
    public List<ResponseCardDto> returnResponseCardDtoList(Long id){
        List<Card> cardList = cardRepository.findAllByCategoryId(id);

        List<ResponseCardDto> cardDtoList = new ArrayList<>();

        for (Card card : cardList) {
            ResponseCardDto responseCardDto = new ResponseCardDto();
            Long cardId = card.getId();
            responseCardDto.setId(cardId);
            responseCardDto.setText(card.getText());
            /**
             * userId 부분 나중에 바꿔야됨!
             */
            responseCardDto.setCardScore(cardScoreRepository.findByCardIdAndUserId(cardId, 1L).map(CardScore::getHighestScore).orElse(0));  //사용자 점수가 없으면 0점
            responseCardDto.setWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, 1L));
            responseCardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, 1L));
            cardDtoList.add(responseCardDto);
        }

        return cardDtoList;
    }
}
