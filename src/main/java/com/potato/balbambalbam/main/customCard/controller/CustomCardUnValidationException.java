package com.potato.balbambalbam.main.customCard.controller;

import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.customCard.dto.CustomCardRequestDto;
import com.potato.balbambalbam.main.customCard.service.CustomCardService;
import com.potato.balbambalbam.main.exception.CardDeleteException;
import com.potato.balbambalbam.main.exception.CardGenerationFailException;
import com.potato.balbambalbam.main.exception.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Tag(name = "CustomCard 생성 & 삭제", description = "CustomCard API")
public class CustomCardUnValidationException {

    private final CustomCardService customCardService;
    @PostMapping("/cards/custom")
    @Operation(summary = "customCard 생성", description = "text 해당하는 custom card를 생성한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드 생성 완료"),
            @ApiResponse(responseCode = "400", description = "ERROR : 카드를 생성 불가(10개 이상 or 한국어 X, 35자 이상)", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity postCustomCard(@RequestBody CustomCardRequestDto customCardRequestDto) throws CardGenerationFailException {
        customCardService.createCustomCardIfPossible(customCardRequestDto.getText(), MyConstant.TEMPORARY_USER_ID);

        return ResponseEntity.ok("카드 생성 완료");
    }

    @DeleteMapping("/cards/custom/{cardId}")
    @Operation(summary = "customCard 삭제", description = "원하는 custom card를 삭제한다")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드 삭제 완료", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 카드 삭제 불가(10개 이상 or 한국어 X, 35자 이상)", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity deleteCustomCard(@PathVariable("cardId") Long cardId) throws CardDeleteException {
        boolean isDeleted = customCardService.deleteCustomCard(cardId);

        if(!isDeleted){
            throw new CardDeleteException(cardId + " : 카드 삭제 실패하였습니다");
        }

        return ResponseEntity.ok("카드 삭제 완료");
    }
}
