package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.IncomingDto;
import ru.practicum.OutgoingDto;
import ru.practicum.mapper.StatMapper;
import ru.practicum.repository.StatRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class StatServiceImpl implements StatService {
    private final StatRepository repository;

    @Override
    public void createStat(IncomingDto incomingDto) {
        repository.save(StatMapper.toHitFromIncomingDto(incomingDto));
    }

    @Override
    @Transactional(readOnly = true)
    public List<OutgoingDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        return repository.getStat(start, end, uris, unique);
    }
}