package ru.practicum.server.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.api.compilation.CompilationDto;
import ru.practicum.main.api.compilation.NewCompilationDto;
import ru.practicum.main.api.compilation.UpdateCompilationRequest;
import ru.practicum.pageable.OffsetBasedPageRequest;
import ru.practicum.server.entity.Compilation;
import ru.practicum.server.entity.Event;
import ru.practicum.server.entity.QCompilation;
import ru.practicum.server.entity.QEvent;
import ru.practicum.server.mapper.CompilationMapper;
import ru.practicum.server.repository.CompilationRepository;
import ru.practicum.server.repository.EventRepository;
import ru.practicum.server.service.CompilationService;
import ru.practicum.main.util.exception.NotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static ru.practicum.constants.Constants.SORT_BY_ID_ASC;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    @Transactional
    public CompilationDto create(NewCompilationDto dto) {
        Compilation compilation = mapper.toCompilation(dto);

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(QEvent.event.id.in(dto.getEvents()));
            Set<Event> events = StreamSupport
                    .stream(eventRepository.findAll(builder).spliterator(), false)
                    .collect(Collectors.toSet());
            compilation.setEvents(events);
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return mapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        getCompilationById(compId);

        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto update(UpdateCompilationRequest dto, Long compId) {
        Compilation compilation = getCompilationById(compId);

        if (dto.getTitle() != null && !dto.getTitle().isBlank()) {
            compilation.setTitle(dto.getTitle());
        }
        if (dto.getPinned() != null) {
            compilation.setPinned(dto.getPinned());
        }

        if (dto.getEvents() != null && !dto.getEvents().isEmpty()) {
            BooleanBuilder builder = new BooleanBuilder();
            builder.and(QEvent.event.id.in(dto.getEvents()));
            Collection<Event> events = StreamSupport
                    .stream(eventRepository.findAll(builder, SORT_BY_ID_ASC).spliterator(), false)
                    .collect(Collectors.toList());
            compilation.setEvents(new HashSet<>(events));
        }
        return mapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    public Collection<CompilationDto> getAll(Boolean pinned, int from, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        if (pinned != null) {
            builder.and(QCompilation.compilation.pinned.eq(pinned));
        }

        Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_ID_ASC);

        return compilationRepository.findAll(builder, pageable)
                .getContent()
                .stream()
                .map(mapper::toCompilationDto)
                .collect(Collectors.toList());
    }

    @Override
    public CompilationDto getById(Long compId) {
        return mapper.toCompilationDto(getCompilationById(compId));
    }

    private Compilation getCompilationById(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(
                () -> new NotFoundException("Compilation with ID = " + compId + " does not exists")
        );
    }
}
