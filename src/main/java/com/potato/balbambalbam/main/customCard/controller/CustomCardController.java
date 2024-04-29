package com.potato.balbambalbam.main.customCard.controller;

import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.customCard.service.CustomCardService;
import com.potato.balbambalbam.main.exception.CardCapacityExceededException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CustomCardController {

    private final CustomCardService customCardService;
    @PostMapping("/cards/custom")
    public ResponseEntity postCustomCard(@RequestBody String text) throws CardCapacityExceededException {
        Long customCardId = customCardService.createCustomCardIfPossible(text, MyConstant.TEMPORARY_USER_ID);

        return ResponseEntity.ok("문장 생성 완료");
    }
}
