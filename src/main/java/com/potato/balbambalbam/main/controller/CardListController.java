package com.potato.balbambalbam.main.controller;

import com.potato.balbambalbam.main.dto.Response;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import com.potato.balbambalbam.main.service.CardListService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CardListController {

    private final CardListService cardListService;

    @GetMapping ("/cards")
    public ResponseEntity<List<ResponseCardDto>> getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        List<ResponseCardDto> cardDtoList = cardListService.resolveCardListRequest(category, subcategory);
        return ResponseEntity.ok().body(cardDtoList);
    }


}
