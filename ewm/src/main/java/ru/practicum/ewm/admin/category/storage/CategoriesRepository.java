package ru.practicum.ewm.admin.category.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.admin.category.model.Category;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
}
