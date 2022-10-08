package ru.practicum.ewm.admin.category.dto;

import ru.practicum.ewm.admin.category.model.Category;

public class CategoryMapper {
    public static Category toCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    public static CategoryDto toCategoryDto(Category category) {
        return new CategoryDto(category.getName(), category.getId());
    }

    public static CategoryDto toCategoryDto(NewCategoryDto newCategoryDto) {
        return new CategoryDto(newCategoryDto.getName(), null);
    }
}
