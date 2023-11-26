package ru.practicum.server.controller.category;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.api.category.CategoryDto;
import ru.practicum.server.service.CategoryService;

import javax.validation.constraints.Min;
import java.util.Collection;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories")
@Validated
public class PublicCategoryController {
    private final CategoryService service;

    @GetMapping("/{catId}")
    public CategoryDto getById(@PathVariable @Min(1) long catId) {
        return service.getById(catId);
    }

    @GetMapping
    public Collection<CategoryDto> getAll(@RequestParam(defaultValue = "0") @Min(0) int from,
                                          @RequestParam(defaultValue = "10") @Min(1) int size) {
        return service.getAll(from, size);
    }
}
