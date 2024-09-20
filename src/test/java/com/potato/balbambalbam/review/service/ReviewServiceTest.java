package com.potato.balbambalbam.review.service;

import com.potato.balbambalbam.data.entity.Card;
import com.potato.balbambalbam.data.entity.CardScore;
import com.potato.balbambalbam.data.entity.Category;
import com.potato.balbambalbam.data.repository.*;
import com.potato.balbambalbam.exception.CategoryNotFoundException;
import com.potato.balbambalbam.review.dto.CardDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@DisplayName("[Service] - 복습")
class ReviewServiceTest {
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CardRepository cardRepository;
    @Mock
    private CardScoreRepository cardScoreRepository;
    @Mock
    private CardWeakSoundRepository cardWeakSoundRepository;
    @Mock
    private CardBookmarkRepository cardBookmarkRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Category parentCategory;
    private Category subCategory;
    private Card card;
    private CardScore cardScore;

    @BeforeEach
    void setUp() {
        parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setName("parent");

        subCategory = new Category();
        subCategory.setId(2L);
        subCategory.setName("sub");
        subCategory.setParentId(1L);

        card = new Card();
        card.setId(1L);
        card.setText("Test Card");
        card.setCategoryId(2L);

        cardScore = new CardScore();
        cardScore.setHighestScore(80);
    }

    @Test
    void getCardsByCategory_Success() {
        when(categoryRepository.findByName("parent")).thenReturn(Optional.of(parentCategory));
        when(categoryRepository.findByNameAndParentId("sub", 1L)).thenReturn(Optional.of(subCategory));
        when(cardRepository.findAllByCategoryId(2L)).thenReturn(Arrays.asList(card));
        when(cardScoreRepository.findByCardIdAndUserId(1L, 1L)).thenReturn(Optional.of(cardScore));

        List<CardDto> result = reviewService.getCardsByCategory("parent", "sub", 1L);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
    }

    @Test
    void getCardsByCategory_CategoryNotFound() {
        when(categoryRepository.findByName("nonexistent")).thenReturn(Optional.empty());

        assertThrows(CategoryNotFoundException.class, () ->
                reviewService.getCardsByCategory("nonexistent", "sub", 1L)
        );
    }

    @Test
    void isReviewCard_True() {
        when(cardScoreRepository.findByCardIdAndUserId(1L, 1L)).thenReturn(Optional.of(cardScore));

        assertTrue(reviewService.isReviewCard(card, 1L, 2L));
    }

    @Test
    void isReviewCard_False_NoScore() {
        when(cardScoreRepository.findByCardIdAndUserId(1L, 1L)).thenReturn(Optional.empty());

        assertFalse(reviewService.isReviewCard(card, 1L, 2L));
    }

    @Test
    void isReviewCard_False_PerfectScore() {
        cardScore.setHighestScore(100);
        when(cardScoreRepository.findByCardIdAndUserId(1L, 1L)).thenReturn(Optional.of(cardScore));

        assertFalse(reviewService.isReviewCard(card, 1L, 2L));
    }

    @Test
    void convertCardToDto_Success() {
        when(cardScoreRepository.findByCardIdAndUserId(1L, 1L)).thenReturn(Optional.of(cardScore));
        when(cardWeakSoundRepository.existsByCardIdAndUserId(1L, 1L)).thenReturn(true);
        when(cardBookmarkRepository.existsByCardIdAndUserId(1L, 1L)).thenReturn(false);

        CardDto result = reviewService.convertCardToDto(card, 1L);

        assertEquals(1L, result.getId());
        assertEquals("Test Card", result.getText());
        assertEquals(80, result.getCardScore());
        assertTrue(result.isWeakCard());
        assertFalse(result.isBookmark());
    }
}