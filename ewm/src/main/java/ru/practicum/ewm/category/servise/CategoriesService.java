package ru.practicum.ewm.category.servise;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoriesRepository;
import ru.practicum.ewm.event.storage.EventStorage;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

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

    private boolean isNameUnique(String name) {
        if (categoriesRepository.findByName(name).isPresent()) {
            return true;
        } else return false;
    }
}
