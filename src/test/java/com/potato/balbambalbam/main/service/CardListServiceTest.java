package com.potato.balbambalbam.main.service;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
        Long requestCategory = cardListService.findRequestCategory(category, subCategory);
        Long requestCategory2 = cardListService.findRequestCategory(category2, subCategory);

        //then
        Assertions.assertThat(requestCategory).isEqualTo(8);
        Assertions.assertThat(requestCategory2).isEqualTo(15);
    }
}