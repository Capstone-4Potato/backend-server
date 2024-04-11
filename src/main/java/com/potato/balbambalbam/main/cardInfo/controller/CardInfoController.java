package com.potato.balbambalbam.main.cardInfo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
public class CardInfoController {

    @GetMapping("cards/{cardId}")
    @Operation(summary = "card info 제공", description = "카드 id, 카드 text, 발음 text, 맞춤 음성 제공")
    @ApiResponses()
    public ResponseEntity postCardInfo(@PathVariable("cardId") Long cardId){

        return null;
    }
}
