package ru.practicum.ewm.category.service;

import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;

import java.util.List;

public interface CategoryService {

    CategoryDto addCategory(NewCategoryDto dto);

    CategoryDto updateCategory(long id, CategoryDto dto);

    List<CategoryDto> getAllCategories(int from, int size);

    CategoryDto getCategoryById(long id);

    void deleteCategoryById(long id);
}
