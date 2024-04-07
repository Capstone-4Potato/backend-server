package com.potato.balbambalbam.main.controller;

import com.potato.balbambalbam.main.dto.CardListResponse;
import com.potato.balbambalbam.main.dto.MainExceptionResolverController;
import com.potato.balbambalbam.main.dto.ResponseCardDto;
import com.potato.balbambalbam.main.exception.ExceptionDto;
import com.potato.balbambalbam.main.service.CardListService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "CardList", description = "CardList API")
public class CardListController {

    private final CardListService cardListService;

    @GetMapping ("/cards")
    @Operation(summary = "CardList 조회", description = "parameter에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponse<List<ResponseCardDto>>> getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        List<ResponseCardDto> cardDtoList = cardListService.resolveCardListRequest(category, subcategory);
        CardListResponse<List<ResponseCardDto>> response = new CardListResponse<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(response);
    }

}
