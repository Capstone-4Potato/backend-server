package com.potato.balbambalbam.main.cardInfo.service;

import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.cardInfo.dto.VoiceRequestDto;
import com.potato.balbambalbam.main.cardInfo.exception.UserNotFoundException;
import com.potato.balbambalbam.main.cardList.exception.CardNotFoundException;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;
@SpringBootTest
class CardInfoServiceTest {

    @Autowired
    CardInfoService cardInfoService;
    @Test
    void getCardInfoTest(){
        //given
        Long userId = 1L;
        Long cardId = 194L; //수달

        //when
        CardInfoResponseDto cardInfo = cardInfoService.getCardInfo(cardId, userId);

        //then
        assertThat(cardInfo.getText()).isEqualTo("수달");
        assertThat(cardInfo.getPronunciation()).isEqualTo("수달");
    }

    @Test
    void getCardInfoErrorTest(){
        //given
        Long userId = 1L;
        Long errorCardId = 12345L; //수달

        //when then
        org.junit.jupiter.api.Assertions.assertThrows(CardNotFoundException.class, ()-> cardInfoService.getCardInfo(errorCardId, userId));
    }

    @Test
    void getUserInfoTest(){
        //given
        Long userId = 1L;
        Long userIdError = 12345L;

        //when
        VoiceRequestDto userInfo = cardInfoService.getUserInfo(userId);

        //then
        assertThat(userInfo.getGender()).isEqualTo(0);
        org.junit.jupiter.api.Assertions.assertThrows(UserNotFoundException.class, () -> cardInfoService.getUserInfo(userIdError));
    }
}