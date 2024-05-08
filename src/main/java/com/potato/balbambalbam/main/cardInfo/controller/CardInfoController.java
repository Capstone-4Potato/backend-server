package com.potato.balbambalbam.main.cardInfo.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.cardInfo.dto.CardInfoResponseDto;
import com.potato.balbambalbam.main.cardInfo.service.CardInfoService;
import com.potato.balbambalbam.exception.ExceptionDto;
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
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CardInfo", description = "CardInfo API")
public class CardInfoController {
    //TODO : user 동적으로 할당
    private final CardInfoService cardInfoService;

    @GetMapping("/cards/{cardId}")
    @Operation(summary = "card tts 제공", description = "맞춤 음성 제공")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK : 카드 정보, 음성 제공 성공"),
                    @ApiResponse(responseCode = "400", description = "ERROR : 카드 또는 회원 조회 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "ERROR : 카드 음성 생성 실패",  content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<CardInfoResponseDto> getCardInfo(@PathVariable("cardId") Long cardId) throws JsonProcessingException {
        log.info("[음성 요청]");

        CardInfoResponseDto cardInfoResponseDto = cardInfoService.getCardInfo(MyConstant.TEMPORARY_USER_ID, cardId);
        return ResponseEntity.ok().body(cardInfoResponseDto);
    }

    @GetMapping("/cards/custom/{cardId}")
    @Operation(summary = "custom card tts 제공", description = "맞춤 음성 제공")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK : 카드 정보, 음성 제공 성공"),
                    @ApiResponse(responseCode = "400", description = "ERROR : 카드 또는 회원 조회 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "ERROR : 카드 음성 생성 실패",  content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<CardInfoResponseDto> postCustomCardInfo(@PathVariable("cardId") Long cardId) throws JsonProcessingException {
        CardInfoResponseDto cardInfoResponseDto = cardInfoService.getCustomCardInfo(MyConstant.TEMPORARY_USER_ID, cardId);

        return ResponseEntity.ok().body(cardInfoResponseDto);
    }
}
