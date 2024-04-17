package com.potato.balbambalbam.main.cardFeedback.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.potato.balbambalbam.MyConstant;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackRequestDto;
import com.potato.balbambalbam.main.cardFeedback.dto.UserFeedbackResponseDto;
import com.potato.balbambalbam.main.cardFeedback.service.CardFeedbackService;
import com.potato.balbambalbam.main.cardList.dto.ExceptionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;

@RestController
@RequiredArgsConstructor
@Slf4j
@Tag(name = "CardFeedback", description = "CardFeedback API")
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
                                                                    @Validated @RequestBody UserFeedbackRequestDto userFeedbackRequestDto,
                                                                    BindingResult bindingResult) throws JsonProcessingException {
        //요청 validation 에러
        if(bindingResult.hasErrors()){
            log.info("[ERROR]:{}", bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(bindingResult.getAllErrors());
        }

        //성공 로직
        UserFeedbackResponseDto userFeedbackResponseDto = cardFeedbackService.postUserFeedback(userFeedbackRequestDto, MyConstant.TEMPORARY_USER_ID, cardId);

        HttpHeaders headers = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        headers.setContentType(mediaType);

        return ResponseEntity.ok().headers(headers).body(userFeedbackResponseDto);

    }
}
