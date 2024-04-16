package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardBookmark;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.main.cardList.dto.ResponseCardDto;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import com.potato.balbambalbam.main.cardList.exception.CategoryNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CardListService {
    //TODO: userid를 동적으로 처리하도록 변경 필요
    //TODO : service 분리 (list 제공 (getCardsByCategory)/ list update시 사용(북마크랑 취약음 update))
    public static final long TEMPORARY_USER_ID = 1L;
    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardScoreRepository cardScoreRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;

    /**
     * controller getCardList 요청 처리
     * @param category
     * @param subcategory
     * @return cardDtoList
     */
    public List<ResponseCardDto> getCardsByCategory(String category, String subcategory){
        Long requestCategory = getSubCategoryId(category, subcategory);
        List<ResponseCardDto> cardDtoList = createCardDtoListForCategory(requestCategory);

        return cardDtoList;
    }

    /**
     * 세부 카테고리 아이디 반환
     * @param subcategory
     * @return requestCategoryId
     */
    protected Long getSubCategoryId(String category, String subcategory){
        //부모 카테고리 아이디 찾기
        Long parentId = categoryRepository.findByName(category).map(Category::getId).orElseThrow(()->new CategoryNotFoundException("잘못된 URL 요청입니다"));
        //하위 카테고리 아이디 찾기
        Long requestCategoryId = categoryRepository.findByNameAndParentId(subcategory, parentId).map(Category::getId).orElseThrow(()->new CategoryNotFoundException("잘못된 URL 요청입니다"));

        return requestCategoryId;
    }

    /**
     * 카테고리에 맞는 카드 DTO 리스트 반환
     * @param id
     * @return cardDtoList
     */
    protected List<ResponseCardDto> createCardDtoListForCategory(Long id){
        List<Card> cardList = cardRepository.findAllByCategoryId(id);
        List<ResponseCardDto> cardDtoList = new ArrayList<>();
        cardList.stream().forEach(card -> cardDtoList.add(convertCardToDto(card)));

        return cardDtoList;
    }

    /**
     * Card Entity를 ResponseCardDto에 맞게 변환
     * @param card
     * @return
     */
    protected ResponseCardDto convertCardToDto(Card card){
        ResponseCardDto responseCardDto = new ResponseCardDto();
        Long cardId = card.getId();
        responseCardDto.setId(cardId);
        responseCardDto.setText(card.getText());

        responseCardDto.setCardScore(cardScoreRepository.findByCardIdAndUserId(cardId, TEMPORARY_USER_ID).map(CardScore::getHighestScore).orElse(0));  //사용자 점수가 없으면 0점
        responseCardDto.setWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, TEMPORARY_USER_ID));
        responseCardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, TEMPORARY_USER_ID));
        responseCardDto.setPronunciation(cardRepository.findById(cardId).map(Card::getPronunciation).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다")));

        return responseCardDto;
    }

    /**
     * 카드 북마크 업데이트
     * @param cardId
     * @param userId
     */
    public String toggleCardBookmark(Long cardId, Long userId){
        cardRepository.findById(cardId).orElseThrow(()->new CardNotFoundException("존재하지 않는 카드입니다."));

        if(cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId)){
            cardBookmarkRepository.deleteByCardIdAndUserId(cardId, userId);
            return cardId + "번 카드 북마크 제거";
        }else{
            CardBookmark cardBookmark = new CardBookmark(userId, cardId);
            cardBookmarkRepository.save(cardBookmark);
            return cardId + "번 카드 북마크 추가";
        }
    }

    //TODO : 취약음 갱신 시 cardWeakSound Update(취약음 Test 완료 시 진행)
//    public String updateCardWeakSound(Long userId){
//        //UPDATE하는 부분
//        List<Card> cardList = cardRepository.findAll();
//
//        cardList.forEach(card -> {
//            List<Long> phonemes = card.getPhonemesMap();
//            if(!Collections.disjoint(phonemes, userWeakSoundList)){
//                cardWeakSoundRepository.save(new CardWeakSound(userId ,card.getId()));
//            }
//        });
//
//        return "카드 취약음 갱신 성공";
//    }
}