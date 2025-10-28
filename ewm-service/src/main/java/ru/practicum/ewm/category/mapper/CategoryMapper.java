package ru.practicum.ewm.category.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.category.dto.NewCategoryDto;
import ru.practicum.ewm.category.model.Category;

import java.util.List;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface CategoryMapper {

    @Mapping(target = "id", ignore = true)
    Category mapToCategoryFromNewCategoryDto(NewCategoryDto dto);

    CategoryDto mapToCategoryDto(Category category);

    List<CategoryDto> mapToCategoryDtoList(List<Category> categories);

    @Mapping(target = "id", ignore = true)
    void updateCategoryFromDto(CategoryDto dto, @MappingTarget Category category);
}
