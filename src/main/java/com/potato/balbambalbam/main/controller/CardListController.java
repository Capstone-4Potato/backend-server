package com.potato.balbambalbam.main.controller;

import com.potato.balbambalbam.main.service.CardListService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class CardListController {

    private final CardListService cardListService;

    @GetMapping ("/cards")
    public String getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        Long requestCategory = cardListService.returnRequestCategory(category, subcategory);
        return requestCategory.toString();
    }


}
