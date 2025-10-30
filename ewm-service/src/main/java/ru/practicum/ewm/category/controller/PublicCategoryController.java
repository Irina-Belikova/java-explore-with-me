package ru.practicum.ewm.category.controller;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.service.CategoryService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.util.List;

@RestController
@RequestMapping("/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCategoryController {
    private final CategoryService categoryService;
    private final ValidationUtil validation;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<CategoryDto> getAllCategories(@RequestParam(defaultValue = "0")
                                              @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                              @RequestParam(defaultValue = "10")
                                              @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Поступил запрос на получения списка категорий с параметрами from - {} и size - {}.", from, size);
        return categoryService.getAllCategories(from, size);
    }

    @GetMapping("/{catId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CategoryDto getCategoryById(@PathVariable
                                       @Positive(message = "Id категории должно быть положительным числом.") long catId) {
        log.info("Поступил запрос на получение данных категории по id - {}", catId);
        validation.checkCategoryId(catId);
        return categoryService.getCategoryById(catId);
    }
}
