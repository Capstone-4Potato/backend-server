package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.data.entity.*;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CardNotFoundException;
import com.potato.balbambalbam.exception.CategoryNotFoundException;
import com.potato.balbambalbam.main.cardList.dto.ResponseCardDto;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CardListService {
    //TODO : service 분리 (list 제공 (getCardsByCategory)/ list update시 사용(북마크랑 취약음 update))
    private final CategoryRepository categoryRepository;
    private final CardRepository cardRepository;
    private final CardBookmarkRepository cardBookmarkRepository;
    private final CardWeakSoundRepository cardWeakSoundRepository;
    private final CardScoreRepository cardScoreRepository;
    private final CustomCardRepository customCardRepository;
    private final UserWeakSoundRepository userWeakSoundRepository;
    private final PronunciationPictureRepository pronunciationPictureRepository;
    private final BulkRepository bulkRepository;

    /**
     * controller getCardList 요청 처리
     * @param category
     * @param subcategory
     * @return cardDtoList
     */
    public List<ResponseCardDto> getCardsByCategory(String category, String subcategory, Long userId){
        Long requestCategory = getSubCategoryId(category, subcategory);
        List<ResponseCardDto> cardDtoList = createCardDtoListForCategory(requestCategory, userId);

        return cardDtoList;
    }

    public List<ResponseCardDto> getCustomCards(Long userId){
        List<CustomCard> customCardList = customCardRepository.findAllByUserId(userId);
        List<ResponseCardDto> cardDtoList = new ArrayList<>();

        customCardList.forEach(customCard -> {
                int highestScore = (customCard.getHighestScore() == null) ? 0 : customCard.getHighestScore();
                cardDtoList.add(new ResponseCardDto
                (customCard.getId(), customCard.getText(), customCard.getPronunciation(), customCard.getEngPronunciation(),
                        customCard.getIsBookmarked(), false, highestScore,
                        null, null ));
        });

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
     */
    protected List<ResponseCardDto> createCardDtoListForCategory(Long categoryId, Long userId){
        List<Card> cardList = cardRepository.findAllByCategoryId(categoryId);
        List<ResponseCardDto> cardDtoList = new ArrayList<>();

        cardList.stream().forEach(card -> cardDtoList.add(convertCardToDto(card, userId)));

        return cardDtoList;
    }

    /**
     * Card Entity를 ResponseCardDto에 맞게 변환
     * @param card
     * @return
     */
    protected ResponseCardDto convertCardToDto(Card card, Long userId){
        ResponseCardDto responseCardDto = new ResponseCardDto();

        Long cardId = card.getId();
        responseCardDto.setId(cardId);
        responseCardDto.setText(card.getText());

        responseCardDto.setCardScore(cardScoreRepository.findByCardIdAndUserId(cardId, userId).map(CardScore::getHighestScore).orElse(0));  //사용자 점수가 없으면 0점
        responseCardDto.setWeakCard(cardWeakSoundRepository.existsByCardIdAndUserId(cardId, userId));
        responseCardDto.setBookmark(cardBookmarkRepository.existsByCardIdAndUserId(cardId, userId));
        responseCardDto.setPronunciation(card.getPronunciation());
        responseCardDto.setEngPronunciation(card.getEngPronunciation());

        //음절이라면 사진과 설명 제공
        if(card.getCategoryId() <= 14){
            Long phonemeId = null;
            //모음인 경우
            if(card.getCategoryId() <= 7){
                phonemeId = card.getPhonemesMap().get(1);
            }
            //자음인 경우
            else{
                phonemeId = card.getPhonemesMap().get(0);
            }
            PronunciationPicture pronunciationPicture = pronunciationPictureRepository.findByPhonemeId(phonemeId).orElseThrow(() -> new IllegalArgumentException("음절 설명 찾기에 실패했습니다"));
            responseCardDto.setPicture(pronunciationPicture.getPicture());
            responseCardDto.setExplanation(pronunciationPicture.getExplanation());
        }else{
            responseCardDto.setPicture(null);
            responseCardDto.setExplanation(null);
        }

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

    public String toggleCustomCardBookmark(Long cardId){
        CustomCard customCard = customCardRepository.findById(cardId).orElseThrow(() -> new CardNotFoundException("존재하지 않는 카드입니다."));

        if(customCard.getIsBookmarked()){
            customCard.setIsBookmarked(false);
            customCardRepository.save(customCard);
            return cardId + "번 카드 북마크 제거";
        }else{
            customCard.setIsBookmarked(true);
            customCardRepository.save(customCard);
            return cardId + "번 카드 북마크 추가";
        }
    }

    /**
     * 취약음 갱신 시 user weaksound table update
     * @param userId
     * @return
     */
    public String updateCardWeakSound(Long userId){
        //card weaksound 테이블 해당 userId 행 전부 삭제
        bulkRepository.deleteAllByUserId(userId);

        List<Card> cardList = getCardListWithoutSentence();

        List<Long> phonemeList = getPhonemeList(userId);

        List<CardWeakSound> cardWeakSoundList = new ArrayList<>();
        cardList.forEach(card -> {
            List<Long> phonemes = card.getPhonemesMap();
            if(!Collections.disjoint(phonemes, phonemeList)){
                cardWeakSoundList.add(new CardWeakSound(userId ,card.getId()));
            }
        });
        bulkRepository.saveAll(cardWeakSoundList);

        return "카드 취약음 갱신 성공";
    }

    protected List<Card> getCardListWithoutSentence(){
        List<Long> categoryIds = Arrays.asList(
                5L, 6L, 7L, 8L, 9L, 10L, 11L, 12L, 13L,
                14L, 15L, 16L, 17L, 18L, 19L, 20L, 21L, 22L,
                23L, 24L, 25L, 26L, 27L, 28L, 29L, 30L, 31L
        );
        List<Card> cardList = cardRepository.findByCategoryIdIn(categoryIds);

        return cardList;
    }

    protected List<Long> getPhonemeList(Long userId){
        List<UserWeakSound> userWeakSoundList = userWeakSoundRepository.findAllByUserId(userId);
        List<Long> phonemeList = new ArrayList<>();

        userWeakSoundList.forEach(userWeakSound -> {
            phonemeList.add(userWeakSound.getUserPhoneme());
        });

        return phonemeList;
    }
}
