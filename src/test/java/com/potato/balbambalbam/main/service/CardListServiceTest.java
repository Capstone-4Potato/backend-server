package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.entity.Card;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

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
        Assertions.assertThat(requestCategory).isEqualTo(8);
        Assertions.assertThat(requestCategory2).isEqualTo(15);
    }

    @Test
    void testReturnResponseCardDtoList(){
        //given
        Long categoryId = 5L;    //음절 - 단모음 ( 아어오우.. )

        //then
        List<ResponseCardDto> cardDtoList = cardListService.returnResponseCardDtoList(categoryId);

        //
        for (ResponseCardDto responseCardDto : cardDtoList) {
            System.out.println("responseCardDto = " + responseCardDto);
        }

    }

}
