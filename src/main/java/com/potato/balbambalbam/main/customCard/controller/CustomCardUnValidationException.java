package com.potato.balbambalbam.main.customCard.controller;

import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.customCard.service.CustomCardService;
import com.potato.balbambalbam.main.exception.CardDeleteException;
import com.potato.balbambalbam.main.exception.CardGenerationFailException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CustomCardUnValidationException {

    private final CustomCardService customCardService;
    @PostMapping("/cards/custom")
    public ResponseEntity postCustomCard(@RequestBody String text) throws CardGenerationFailException {
        customCardService.createCustomCardIfPossible(text, MyConstant.TEMPORARY_USER_ID);

        return ResponseEntity.ok("문장 생성 완료");
    }

    @DeleteMapping("/cards/custom/{cardId}")
    public ResponseEntity deleteCustomCard(@PathVariable("cardId") Long cardId) throws CardDeleteException {
        boolean isDeleted = customCardService.deleteCustomCard(cardId);

        if(!isDeleted){
            throw new CardDeleteException(cardId + " : 카드 삭제 실패하였습니다");
        }

        return ResponseEntity.ok("문장 삭제 완료");
    }
}
