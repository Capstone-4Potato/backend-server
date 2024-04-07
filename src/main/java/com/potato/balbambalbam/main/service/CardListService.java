package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.entity.CardScore;
import com.potato.balbambalbam.entity.Category;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import com.potato.balbambalbam.main.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class CardListService {
    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardScoreRepository cardScoreRepository;

    /**
     * controller getCardList 요청 처리
     * @param category
     * @param subcategory
     * @return cardDtoList
     */
    public List<ResponseCardDto> resolveCardListRequest(String category, String subcategory){
        Long requestCategory = returnRequestCategory(category, subcategory);
        List<ResponseCardDto> cardDtoList = returnResponseCardDtoList(requestCategory);
        return cardDtoList;
    }

    /**
     * 세부 카테고리 아이디 반환
     * @param subcategory
     * @return requestCategoryId
     */
    protected Long returnRequestCategory(String category, String subcategory){
        //부모 카테고리 아이디 찾기
        Long parentId = categoryRepository.findByName(category).map(Category::getId).orElseThrow(()->new NoSuchElementException("잘못된 요청입니다"));
        //하위 카테고리 아이디 찾기
        Long requestCategoryId = categoryRepository.findByNameAndParentId(subcategory, parentId).getId();

        return requestCategoryId;
    }

    /**
     * 카테고리에 맞는 카드 DTO 리스트 반환
     * @param id
     * @return cardDtoList
     */
    protected List<ResponseCardDto> returnResponseCardDtoList(Long id){
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
