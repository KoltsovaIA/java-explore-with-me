package ru.practicum.server.service;

import ru.practicum.main.api.compilation.CompilationDto;
import ru.practicum.main.api.compilation.NewCompilationDto;
import ru.practicum.main.api.compilation.UpdateCompilationRequest;

import java.util.Collection;

public interface CompilationService {

    CompilationDto create(NewCompilationDto dto);

    CompilationDto update(UpdateCompilationRequest dto, Long compId);

    Collection<CompilationDto> getAll(Boolean pinned, int from, int size);

    CompilationDto getById(Long compId);

    void delete(Long compId);
}
