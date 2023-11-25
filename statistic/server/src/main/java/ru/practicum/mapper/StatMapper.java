package ru.practicum.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.IncomingDto;
import ru.practicum.model.Hit;

@UtilityClass
public class StatMapper {
    public Hit toHitFromIncomingDto(IncomingDto incomingDto) {
        return Hit.builder()
                .app(incomingDto.getApp())
                .uri(incomingDto.getUri())
                .ip(incomingDto.getIp())
                .timestamp(incomingDto.getTimestamp())
                .build();
    }
}