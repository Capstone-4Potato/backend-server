package com.potato.balbambalbam.main.customCard.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomCardController {

    @PostMapping("/cards/custom")
    public ResponseEntity postCustomCard(@RequestBody String text){

        return ResponseEntity.ok("문장 생성 완료");
    }
}
