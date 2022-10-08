package ru.practicum.ewm.category.servise;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.CategoryMapper;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.storage.CategoriesRepository;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CategoriesServicePublic {
    @Autowired
    private final CategoriesRepository categoriesRepository;

    //Получение категорий
    public List<CategoryDto> findAll(String from, String size) {
        try {
            Integer pageFrom = Integer.parseInt(from);
            Integer sizeUrl = Integer.parseInt(size);
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(pageFrom, sizeUrl, sort);
            Page<Category> categoriesPage;
            int allPage;
            categoriesPage = categoriesRepository.findAll(page);
            allPage = categoriesPage.getTotalPages();
            if (pageFrom >= allPage) {
                if (allPage > 0) {
                    --allPage;
                }
                categoriesPage = categoriesRepository.findAll(PageRequest.of(allPage, sizeUrl, sort));
            }
            return categoriesPage.stream()
                    .map(categories -> CategoryMapper.toCategoryDto(categories))
                    .collect(Collectors.toList());

        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    //Получение информации о категории по её идентификатору
    public CategoryDto getCategoryById(String stringId) {
        try {
            Long id = Long.parseLong(stringId);
            return CategoryMapper.toCategoryDto(categoriesRepository.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id))));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }
}
