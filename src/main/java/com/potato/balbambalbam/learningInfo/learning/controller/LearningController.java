package com.potato.balbambalbam.learningInfo.learning.controller;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.learningInfo.learning.dto.LearningResponseDto;
import com.potato.balbambalbam.learningInfo.learning.service.LearningService;
import com.potato.balbambalbam.profile.token.jwt.JWTUtil;
import com.potato.balbambalbam.profile.join.service.JoinService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@ResponseBody
@Slf4j
@Tag(name = "LearningProgress API", description = "사용자의 학습 진척도(음절, 단어, 문장)를 백분율로 제공한다.")
public class LearningController {

    private final LearningService learningService;
    private final JoinService joinService;
    private final JWTUtil jwtUtil;

    public LearningController (LearningService learningService,
                               JoinService joinService,
                               JWTUtil jwtUtil){
        this.learningService = learningService;
        this.joinService = joinService;
        this.jwtUtil = jwtUtil;
    }
    private Long extractUserIdFromToken(String access) { // access 토큰으로부터 userId 추출하는 함수
        String socialId = jwtUtil.getSocialId(access); 
        return joinService.findUserBySocialId(socialId).getId(); 
    }

    @Operation(summary = "사용자의 학습 진척도 조회", description = "사용자의 카테고리별 학습 진척도(%)를 제공한다.")
    @ApiResponses(
            value = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "사용자의 학습 진척도를 가지고 온 경우",
                            content = @Content(mediaType = "application/json",
                                    schema = @Schema(implementation = LearningResponseDto.class))
                    )
            }
    )
    @GetMapping("/learning/progress")
    public ResponseEntity<LearningResponseDto> getLearningProgress(@RequestHeader("access") String access){
        Long userId = extractUserIdFromToken(access);

        List<CardScore> scores = learningService.findCardScoresByUserId(userId); // 사용자 카드 중 점수를 받은 카드
        List<Card> allCards = learningService.findAllCards(); // 전체 카드
        Map<Long, Card> cardDetails = allCards.stream().collect(Collectors.toMap(Card::getId, card -> card));

        // 카드 카테고리 번호 유의하기!
        int syllableCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 1 && card.getCategoryId() <= 14).count();
        int wordCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 15 && card.getCategoryId() <= 31).count();
        int sentenceCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 32 && card.getCategoryId() <= 35).count();

        int syllableScoreCount = 0, wordScoreCount = 0, sentenceScoreCount = 0;
        for (CardScore score : scores) {
            Card card = cardDetails.get(score.getCardId());
            if (card.getCategoryId() >= 1 && card.getCategoryId() <= 14) {
                syllableScoreCount++;
            } else if (card.getCategoryId() >= 15 && card.getCategoryId() <= 31) {
                wordScoreCount++;
            } else if (card.getCategoryId() >= 32 && card.getCategoryId() <= 35) {
                sentenceScoreCount++;
            }
        }

        double syllableProgress = syllableCount > 0 ?
                (double) syllableScoreCount / syllableCount * 100 : 0;
        double wordProgress = wordCount > 0 ?
                (double) wordScoreCount / wordCount * 100 : 0;
        double sentenceProgress = sentenceCount > 0 ?
                (double) sentenceScoreCount / sentenceCount * 100 : 0;

        LearningResponseDto response = new LearningResponseDto();
        response.setSyllableProgress(syllableProgress);
        response.setWordProgress(wordProgress);
        response.setSentenceProgress(sentenceProgress);

        return ResponseEntity.ok(response);
    }
}
