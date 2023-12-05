package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.api.event.LocationDto;
import ru.practicum.server.entity.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    Location toLocation(LocationDto locationDto);
}
