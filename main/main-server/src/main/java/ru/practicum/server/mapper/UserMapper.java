package ru.practicum.server.mapper;

import org.mapstruct.Mapper;
import ru.practicum.main.api.user.NewUserRequest;
import ru.practicum.main.api.user.UserDto;
import ru.practicum.main.api.user.UserShortDto;
import ru.practicum.server.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(NewUserRequest newUserRequest);

    UserDto toUserDto(User user);

    UserShortDto toUserShortDto(User user);
}
