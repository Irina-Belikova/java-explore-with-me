package ru.practicum.ewm.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper mapper;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto dto) {
        Category category = mapper.mapToCategoryFromNewCategoryDto(dto);
        category = categoryRepository.save(category);
        return mapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(long id, CategoryDto dto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с таким id - %s не найдена.", id)));
        mapper.updateCategoryFromDto(dto, category);
        return mapper.mapToCategoryDto(category);
    }

    @Override
    public List<CategoryDto> getAllCategories(int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Category> categories = categoryRepository.findAll(page).getContent();
        return mapper.mapToCategoryDtoList(categories);
    }

    @Override
    public CategoryDto getCategoryById(long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Категория с таким id - %s не найдена.", id)));
        return mapper.mapToCategoryDto(category);
    }

    @Override
    @Transactional
    public void deleteCategoryById(long id) {
        categoryRepository.deleteById(id);
    }
}
