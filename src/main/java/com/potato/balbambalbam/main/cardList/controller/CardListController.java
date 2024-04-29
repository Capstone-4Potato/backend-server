package com.potato.balbambalbam.main.cardList.controller;

import com.potato.balbambalbam.main.MyConstant;
import com.potato.balbambalbam.main.exception.ExceptionDto;
import com.potato.balbambalbam.main.cardList.dto.CardListResponseDto;
import com.potato.balbambalbam.main.cardList.dto.ResponseCardDto;
import com.potato.balbambalbam.main.cardList.service.CardListService;
import com.potato.balbambalbam.main.cardList.service.UpdatePhonemeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.Charset;
import java.util.List;

import static com.potato.balbambalbam.main.MyConstant.TEMPORARY_USER_ID;

@RestController
@RequiredArgsConstructor
@Tag(name = "CardList", description = "CardList API")
public class CardListController {
    //TODO : user 동적으로 할당
    private final CardListService cardListService;
    private final UpdatePhonemeService updatePhonemeService;

    @GetMapping ("/cards")
    @Operation(summary = "CardList 조회", description = "parameter에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponseDto<List<ResponseCardDto>>> getCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        List<ResponseCardDto> cardDtoList = cardListService.getCardsByCategory(category, subcategory);
        CardListResponseDto<List<ResponseCardDto>> response = new CardListResponseDto<>(cardDtoList, cardDtoList.size());

        //header : json, utf-8 인코딩
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        httpHeaders.setContentType(mediaType);

        return ResponseEntity.ok().headers(httpHeaders).body(response);
    }

    @GetMapping ("/cards/custom")
    @Operation(summary = "Custom CardList 조회", description = "parameter에 맞는 카테고리의 카드 리스트를 조회한다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 카드리스트 조회", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카테고리 조회", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity<CardListResponseDto<List<ResponseCardDto>>> getCustomCardList(@RequestParam("category") String category, @RequestParam("subcategory") String subcategory){
        List<ResponseCardDto> cardDtoList = cardListService.getCustomCards(TEMPORARY_USER_ID);
        CardListResponseDto<List<ResponseCardDto>> response = new CardListResponseDto<>(cardDtoList, cardDtoList.size());

        //header : json, utf-8 인코딩
        HttpHeaders httpHeaders = new HttpHeaders();
        MediaType mediaType = new MediaType("application", "json", Charset.forName("UTF-8"));
        httpHeaders.setContentType(mediaType);

        return ResponseEntity.ok().headers(httpHeaders).body(response);
    }

    @GetMapping("/cards/bookmark/{cardId}")
    @Operation(summary = "Card Bookmark 갱신", description = "해당 카드의 북마크 on / off")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 북마크 UPDATE(있으면 삭제 없으면 추가)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카드", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity updateCardBookmark(@PathVariable("cardId") Integer cardId){
        String message = cardListService.toggleCardBookmark(Long.valueOf(cardId), TEMPORARY_USER_ID);
        return ResponseEntity.ok().body(message);
    }

    @GetMapping("/cards/custom/bookmark/{cardId}")
    @Operation(summary = "Custom Card Bookmark 갱신", description = "해당 카드의 북마크 on / off")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "OK : 북마크 UPDATE(있으면 삭제 없으면 추가)", useReturnTypeSchema = true),
            @ApiResponse(responseCode = "400", description = "ERROR : 존재하지 않는 카드", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
    })
    public ResponseEntity updateCustomCardBookmark(@PathVariable("cardId") Integer cardId){
        String message = cardListService.toggleCustomCardBookmark(Long.valueOf(cardId));
        return ResponseEntity.ok().body(message);
    }

    //TODO : 취약음 갱신 시 cardWeakSound Update Controller(취약음 Test 완료 시 진행)
//    @PostMapping("/cards/weaksound")
//    @Operation(summary = "Card WeakSound 갱신", description = "사용자 취약음 갱신 시 전체 카드에 대한 취약음 여부 갱신")
//    @ApiResponses(value = {
//            @ApiResponse(responseCode = "200", description = "OK : Card WeakSound Update 성공)", useReturnTypeSchema = true),
//            @ApiResponse(responseCode = "400", description = "ERROR : Update 실패", content = @Content(schema = @Schema(implementation = ExceptionDto.class)))
//    })
//    public ResponseEntity updateCardWeakSound(@RequestBody("updateWeaksoundList") List<String> updateWeakSounds){
//        String message = cardListService.updateCardWeakSound(TEMPORARY_USER_ID);
//        return ResponseEntity.ok().body(message);
//    }
}
