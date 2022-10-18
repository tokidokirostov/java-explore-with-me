package ru.practicum.ewm.category.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.servise.CategoriesService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@AllArgsConstructor
public class CategoriesControllerPublic {
    @Autowired
    private final CategoriesService categoriesService;

    //Получение категорий
    @GetMapping
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0") String from,
                                              @RequestParam(defaultValue = "10") String size) {
        log.info("Получен запрос GET /admin/users");
        return categoriesService.findAll(from, size);
    }

    //Получение информации о категории по её идентификатору
    @GetMapping("{id}")
    public CategoryDto getCategoriesById(@PathVariable(name = "id") String id) {
        log.info("Получен запрос GET admin/users/{}", id);
        return categoriesService.getCategoryById(id);
    }
}
