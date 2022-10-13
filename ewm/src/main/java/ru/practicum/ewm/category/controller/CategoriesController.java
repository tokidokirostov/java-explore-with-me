package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.servise.CategoriesService;

@Slf4j
@RestController
@RequestMapping(path = "admin/categories")
@AllArgsConstructor
public class CategoriesController {
    @Autowired
    private final CategoriesService categoriesService;

    //Добавление новой категории
    @PostMapping
    public CategoryDto addCategories(@RequestBody NewCategoryDto newCategoryDto) {
        log.info("---> Получен запрос POST /admin/categories categories - {}", newCategoryDto.toString());
        return categoriesService.addCategoried(CategoryMapper.toCategoryDto(newCategoryDto));
    }

    //Изменение категории
    @PatchMapping
    public CategoryDto changeCategories(@RequestBody CategoryDto categoryDto) {
        log.info("---> Получен запрос PUT /admin/categories categories - {}", categoryDto.toString());
        return categoriesService.changeCategories(categoryDto);
    }

    //Удаление категории
    @DeleteMapping("{id}")
    public void deleteCategories(@PathVariable String id) {
        log.info("---> Получен запрос DELETE admin/categories/{}", id);
        categoriesService.deleteCategories(id);
    }
}
