package com.potato.balbambalbam.main.cardList.service;

import com.potato.balbambalbam.main.cardList.service.CardListService;
import com.potato.balbambalbam.main.cardList.dto.ResponseCardDto;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
class CardListServiceTest {
    @Autowired
    private CardListService cardListService;
    @Test
    void getSubCategoryIdTest(){
        //given
        String category = "음절";
        String category2 = "단어";
        String subCategory = "자음ㄱㅋㄲ";

        //when
        Long requestCategory = cardListService.getSubCategoryId(category, subCategory);
        Long requestCategory2 = cardListService.getSubCategoryId(category2, subCategory);

        //then
        assertThat(requestCategory).isEqualTo(8);
        assertThat(requestCategory2).isEqualTo(15);
    }

    @Test
    void createCardDtoListForCategoryTest(){
        //given
        Long categoryId = 5L;    //음절 - 단모음 ( 아어오우.. )

        //then
        List<ResponseCardDto> cardDtoList = cardListService.createCardDtoListForCategory(categoryId);

        //when
        assertThat(cardDtoList.get(0).getId()).isEqualTo(1);
    }

    @Test
    void getCardsByCategoryTest(){
        //given
        String category = "음절";
        String subCategory = "단모음";

        //then
        List<ResponseCardDto> cardDtoList = cardListService.getCardsByCategory(category, subCategory);
        ResponseCardDto cardDto = cardDtoList.get(0);

        //when
        assertThat(cardDtoList.size()).isEqualTo(8);
        assertThat(cardDto.getText()).isEqualTo("아");
    }

    @Test
    void getSubCategoryIdErrorTest(){
        //given
        String category1 = "ㅂ뤡";
        String category2 = " ";
        String subCategory1 = "abc";
        String subCategory2 = " ";

        //then

        //when
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()->{
            cardListService.getSubCategoryId(category1, subCategory1);
        });
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()->{
            cardListService.getSubCategoryId(category2, category2);
        });
    }
}
