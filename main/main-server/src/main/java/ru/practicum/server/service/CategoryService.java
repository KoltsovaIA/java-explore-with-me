package ru.practicum.server.service;

import ru.practicum.main.api.category.CategoryDto;
import ru.practicum.main.api.category.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {

    CategoryDto create(NewCategoryDto newCategoryDto);

    CategoryDto update(NewCategoryDto newCategoryDto, long catId);

    Collection<CategoryDto> getAll(int from, int size);

    CategoryDto getById(long catId);

    void delete(long catId);
}
