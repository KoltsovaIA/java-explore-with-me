package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.main.api.event.EventFullDto;
import ru.practicum.main.api.event.EventShortDto;
import ru.practicum.main.api.event.NewEventDto;
import ru.practicum.server.entity.Category;
import ru.practicum.server.entity.Event;
import ru.practicum.server.entity.User;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "category", target = "category")
    Event toEvent(NewEventDto newEventDto, Category category, User initiator);

    EventFullDto toEventFullDto(Event event);

    EventShortDto toEventShortDto(Event event);
}
