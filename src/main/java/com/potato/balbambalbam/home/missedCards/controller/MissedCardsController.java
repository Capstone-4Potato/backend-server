package com.potato.balbambalbam.home.missedCards.controller;

import com.potato.balbambalbam.log.dto.ExceptionDto;
import com.potato.balbambalbam.home.missedCards.dto.CardDto;
import com.potato.balbambalbam.home.missedCards.dto.MissedCardResponseDto;
import com.potato.balbambalbam.home.missedCards.service.MissedCardsService;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.token.jwt.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "missedCards API", description = "복습 카드를 제공한다")
public class MissedCardsController {
    private final JoinService joinService;
    private final JWTUtil jwtUtil;
    private final MissedCardsService missedCardsService;

    @GetMapping("/cards/missed")
    @Operation(summary = "복습 CardList 조회", description = "복습 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<MissedCardResponseDto<List<CardDto>>> getCardList(@RequestHeader("access") String access){
        Long userId = joinService.findUserBySocialId(jwtUtil.getSocialId(access)).getId();

        List<CardDto> cardDtoList = missedCardsService.getCards(userId);
        MissedCardResponseDto<List<CardDto>> responseDto = new MissedCardResponseDto<>(cardDtoList, cardDtoList.size());

        return ResponseEntity.ok().body(responseDto);
    }
}