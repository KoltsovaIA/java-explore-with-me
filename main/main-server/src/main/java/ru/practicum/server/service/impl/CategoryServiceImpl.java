package ru.practicum.server.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.api.category.CategoryDto;
import ru.practicum.main.api.category.NewCategoryDto;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.server.entity.Category;
import ru.practicum.server.entity.QEvent;
import ru.practicum.server.mapper.CategoryMapper;
import ru.practicum.server.repository.CategoryRepository;
import ru.practicum.server.repository.EventRepository;
import ru.practicum.server.service.CategoryService;
import ru.practicum.main.util.exception.AlreadyExistsException;
import ru.practicum.main.util.exception.NotFoundException;

import java.util.Collection;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.SORT_BY_ID_ASC;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    @Transactional
    public CategoryDto create(NewCategoryDto newCategoryDto) {
        try {
            return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format(
                    "Category with name %s already exists.", newCategoryDto.getName()
            ));
        }
    }

    @Override
    @Transactional
    public void delete(long catId) {
        getCategoryById(catId);

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(QEvent.event.category.id.in(catId));

        if (eventRepository.count(builder) > 0) {
            throw new AlreadyExistsException("Category with ID = " + catId + " already used.");
        }

        try {
            categoryRepository.deleteById(catId);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("Category with ID = " + catId + " already used.");
        }
    }

    @Override
    @Transactional
    public CategoryDto update(NewCategoryDto newCategoryDto, long catId) {
        Category category = getCategoryById(catId);
        category.setName(newCategoryDto.getName());

        try {
            return categoryMapper.toCategoryDto(categoryRepository.saveAndFlush(category));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException(String.format(
                    "Category with name %s already exists.", newCategoryDto.getName()
            ));
        }
    }

    @Override
    public Collection<CategoryDto> getAll(int from, int size) {
        final Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_ID_ASC);
        return categoryRepository.findAll(pageable)
                .getContent()
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    public CategoryDto getById(long catId) {
        return categoryMapper.toCategoryDto(getCategoryById(catId));
    }

    private Category getCategoryById(long id) {
        return categoryRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Category with ID = " + id + " does not exists.")
        );
    }
}
