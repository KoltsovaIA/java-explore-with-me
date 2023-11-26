package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.api.category.CategoryDto;
import ru.practicum.main.api.category.NewCategoryDto;
import ru.practicum.server.entity.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    Category toCategory(NewCategoryDto newCategoryDto);

    CategoryDto toCategoryDto(Category category);
}
