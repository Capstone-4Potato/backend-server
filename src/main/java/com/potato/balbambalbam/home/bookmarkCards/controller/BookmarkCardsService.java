package com.potato.balbambalbam.home.bookmarkCards.controller;

import com.potato.balbambalbam.home.missedCards.dto.CardDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookmarkCardsService {

    public List<CardDto> getCards(Long userId) {
        return null;
    }
}
