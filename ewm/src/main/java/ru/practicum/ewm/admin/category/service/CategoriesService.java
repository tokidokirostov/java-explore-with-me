package ru.practicum.ewm.admin.category.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.dto.CategoryDto;
import ru.practicum.ewm.admin.category.dto.CategoryMapper;
import ru.practicum.ewm.admin.category.storage.CategoriesRepository;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.storage.EventStorage;

import javax.transaction.Transactional;

@Service
@AllArgsConstructor
public class CategoriesService {
    @Autowired
    private final CategoriesRepository categoriesRepository;
    @Autowired
    private final EventStorage eventStorage;

    //Добавление новой категории
    @Transactional
    public CategoryDto addCategoried(CategoryDto categoryDto) {
        if (categoryDto.getName() == null || isNameUnique(categoryDto.getName()) || categoryDto.getName().isEmpty()) {
            throw new ForbiddenError(String.format("FORBIDDEN"));
        }
        return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategory(categoryDto)));
    }

    //Изменение категории
    @Transactional
    public CategoryDto changeCategories(CategoryDto categoryDto) {
        if (categoriesRepository.findById(categoryDto.getId()).isPresent()) {
            if (categoryDto.getName() == null || isNameUnique(categoryDto.getName()) || categoryDto.getName().isEmpty()) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            return CategoryMapper.toCategoryDto(categoriesRepository.save(CategoryMapper.toCategory(categoryDto)));
        } else throw new UserNotFoundException(String.format("Event with id=%d was not found.", categoryDto.getId()));
    }

    //Удаление категории
    @Transactional
    public void deleteCategories(String stringId) {
        try {
            Long id = Long.parseLong(stringId);
            if (categoriesRepository.findById(id).isEmpty()) {
                throw new UserNotFoundException(String.format("Event with id=%d was not found.", id));
            }
            if (!eventStorage.findByCategoryId(id).isEmpty()) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            categoriesRepository.deleteById(id);
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    private boolean isNameUnique(String name) {
        if (categoriesRepository.findByName(name).isPresent()) {
            return true;
        } else return false;
    }
}
