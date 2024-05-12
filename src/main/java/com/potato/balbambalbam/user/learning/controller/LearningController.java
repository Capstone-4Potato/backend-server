package com.potato.balbambalbam.user.learning.controller;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.user.join.jwt.JWTUtil;
import com.potato.balbambalbam.user.join.service.JoinService;
import com.potato.balbambalbam.user.learning.service.LearningService;
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
    private Long extractUserIdFromToken(String access) {
        String socialId = jwtUtil.getSocialId(access);
        return joinService.findUserBySocialId(socialId).getId();
    }

    @GetMapping("/learning/progress")
    public ResponseEntity<?> getLearningProgress(@RequestHeader("access") String access){
        Long userId = extractUserIdFromToken(access);

        List<CardScore> scores = learningService.findCardScoresByUserId(userId);
        List<Card> allCards = learningService.findAllCards();
        Map<Long, Card> cardDetails = allCards.stream().collect(Collectors.toMap(Card::getId, card -> card));

        int syllableCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 1 && card.getCategoryId() <= 14).count();
        int wordCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 15 && card.getCategoryId() <= 24).count();
        int sentenceCount = (int) allCards.stream()
                .filter(card -> card.getCategoryId() >= 25 && card.getCategoryId() <= 33).count();

        System.out.println("syllableCount : " + syllableCount);
        System.out.println("wordCount : " + wordCount);
        System.out.println("sentenceCount : " + sentenceCount);

        int syllableScoreCount = 0, wordScoreCount = 0, sentenceScoreCount = 0;
        for (CardScore score : scores) {
            Card card = cardDetails.get(score.getCardId());
            if (card.getCategoryId() >= 1 && card.getCategoryId() <= 14) {
                syllableScoreCount++;
            } else if (card.getCategoryId() >= 15 && card.getCategoryId() <= 24) {
                wordScoreCount++;
            } else if (card.getCategoryId() >= 25 && card.getCategoryId() <= 33) {
                sentenceScoreCount++;
            }
        }

        System.out.println("syllableScoreCount : " + syllableScoreCount);
        System.out.println("wordScoreCount : " + wordScoreCount);
        System.out.println("sentenceScoreCount : " + sentenceScoreCount);

        double syllableProgress = syllableCount > 0 ?
                (double) syllableScoreCount / syllableCount * 100 : 0;
        double wordProgress = wordCount > 0 ?
                (double) wordScoreCount / wordCount * 100 : 0;
        double sentenceProgress = sentenceCount > 0 ?
                (double) sentenceScoreCount / sentenceCount * 100 : 0;

        System.out.println("syllableProgress : " + syllableProgress);
        System.out.println("wordProgress : " + wordProgress);
        System.out.println("sentenceProgress : " + sentenceProgress);

        return ResponseEntity.ok(Map.of(
                "syllableProgress", syllableProgress,
                "wordProgress", wordProgress,
                "sentenceProgress", sentenceProgress
        ));
    }
}
