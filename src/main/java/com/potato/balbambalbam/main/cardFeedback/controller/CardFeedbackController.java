package com.potato.balbambalbam.main.cardFeedback.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.exception.ExceptionDto;
import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.service.CardFeedbackService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CardFeedback API", description = "음성 녹음 시 serAudio, userScore, recommendCard, waveform 피드백 제공한다.")
public class CardFeedbackController {
    private final CardFeedbackService cardFeedbackService;

    @PostMapping("/cards/{cardId}")
    @Operation(summary = "card Feedback 제공", description = "userAudio, userScore, recommendCard, waveform 제공")
    @ApiResponses(
            value = {
                    @ApiResponse(responseCode = "200", description = "OK : 카드 피드백 제공 성공", content = @Content(schema = @Schema(implementation = UserFeedbackResponseDto.class))),
                    @ApiResponse(responseCode = "400", description = "ERROR : JSON 형식 오류", content = @Content(schema = @Schema(implementation = ExceptionDto.class))),
                    @ApiResponse(responseCode = "404", description = "ERROR : 존재하지 않는 사용자 or 카드",  content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
            }
    )
    public ResponseEntity<Object> postUserFeedback(@PathVariable("cardId") Long cardId,
                                                   @Validated @RequestBody UserFeedbackRequestDto userFeedbackRequestDto) throws JsonProcessingException {
        //성공 로직
        UserFeedbackResponseDto userFeedbackResponseDto = cardFeedbackService.postUserFeedback(userFeedbackRequestDto, MyConstant.TEMPORARY_USER_ID, cardId);
        log.info("[UserFeedbackResponseDto] : {}", userFeedbackResponseDto);

        return ResponseEntity.ok().body(userFeedbackResponseDto);
    }
}
