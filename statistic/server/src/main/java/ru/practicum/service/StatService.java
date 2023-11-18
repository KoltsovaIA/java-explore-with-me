package ru.practicum.service;

import ru.practicum.IncomingDto;
import ru.practicum.OutgoingDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatService {

    void createStat(IncomingDto hitDto);

    List<OutgoingDto> getStat(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}