package com.potato.balbambalbam.main.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CardListController {

    @GetMapping ("/cards")
    public String getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){

        return null;
    }
}
