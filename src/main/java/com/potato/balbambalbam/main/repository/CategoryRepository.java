package com.potato.balbambalbam.main.repository;

import com.potato.balbambalbam.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Override
    List<Category> findAll();

    Category findByName(String name);
    Category findByNameAndParentId(String name, Long parentId);
}
