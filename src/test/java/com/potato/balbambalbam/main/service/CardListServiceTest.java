package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import org.assertj.core.api.Assertions;
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
    void testFindRequestCategory(){
        //given
        String category = "음절";
        String category2 = "단어";
        String subCategory = "자음ㄱㅋㄲ";

        //when
        Long requestCategory = cardListService.returnRequestCategory(category, subCategory);
        Long requestCategory2 = cardListService.returnRequestCategory(category2, subCategory);

        //then
        assertThat(requestCategory).isEqualTo(8);
        assertThat(requestCategory2).isEqualTo(15);
    }

    @Test
    void testReturnResponseCardDtoList(){
        //given
        Long categoryId = 5L;    //음절 - 단모음 ( 아어오우.. )

        //then
        List<ResponseCardDto> cardDtoList = cardListService.returnResponseCardDtoList(categoryId);

        //when
        assertThat(cardDtoList.get(0).getId()).isEqualTo(1);
    }

    @Test
    void testResolveCardListRequest(){
        //given
        String category = "음절";
        String subCategory = "단모음";

        //then
        List<ResponseCardDto> cardDtoList = cardListService.resolveCardListRequest(category, subCategory);
        ResponseCardDto cardDto = cardDtoList.get(0);

        //when
        assertThat(cardDtoList.size()).isEqualTo(8);
        assertThat(cardDto.getText()).isEqualTo("아");
    }

    @Test
    void returnRequestCategoryErrorTest(){
        //given
        String category1 = "ㅂ뤡";
        String category2 = " ";
        String subCategory1 = "abc";
        String subCategory2 = " ";

        //then

        //when
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()->{
            cardListService.returnRequestCategory(category1, subCategory1);
        });
        org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class, ()->{
            cardListService.returnRequestCategory(category2, category2);
        });


    }
}
