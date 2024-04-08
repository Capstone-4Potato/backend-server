package com.potato.balbambalbam.main.controller;

import com.potato.balbambalbam.main.dto.CardListResponse;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import com.potato.balbambalbam.main.dto.ExceptionDto;
import com.potato.balbambalbam.main.service.CardListService;
import com.potato.balbambalbam.main.service.PhonemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CardList", description = "CardList API")
public class CardListController {
    public static final long TEMPORARY_USER_ID = 1L;
    private final CardListService cardListService;
    private final PhonemeService phonemeService;

    @GetMapping ("/cards")
    @Operation(summary = "CardList 조회", description = "parameter에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponse<List<ResponseCardDto>>> getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        List<ResponseCardDto> cardDtoList = cardListService.getCardsByCategory(category, subcategory);
        CardListResponse<List<ResponseCardDto>> response = new CardListResponse<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(response);
    }

    @PostMapping("/cards/bookmark/{cardId}")
    @Operation(summary = "Card Bookmark 갱신", description = "해당 카드의 북마크 on / off")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 북마크 UPDATE(있으면 삭제 없으면 추가)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카드", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity updateCardBookmark(@PathVariable("cardId") Integer cardId){
        String message = cardListService.toggleCardBookmark(Long.valueOf(cardId), TEMPORARY_USER_ID);
        return ResponseEntity.ok().body(message);
    }

    //TODO : CML로만 실행시킬 수 있도록 변경 필요 (삭제)
    @GetMapping("/phonemes")
    public ResponseEntity updateCardPhoneme(){
        phonemeService.updateCardPhonemeColumn();
        return ResponseEntity.ok("업데이트 성공");
    }

}
