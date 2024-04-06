package com.potato.balbambalbam.main.service;

import com.potato.balbambalbam.main.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CardListService {
    private final CategoryRepository categoryRepository;

    //요청하는 카테고리 찾기
    //카테고리에 맞는 카드리스트 찾기
    //카드 리스트 반환
    public Long findRequestCategory(String category, String subcategory){
        //부모 카테고리 아이디 찾기
        Long parentId = categoryRepository.findByName(category).getId();
        //하위 카테고리 아이디 찾기
        Long requestCategoryId = categoryRepository.findByNameAndParentId(subcategory, parentId).getId();

        return requestCategoryId;
    }
}
