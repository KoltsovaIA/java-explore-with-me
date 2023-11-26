package ru.practicum.server.service;

import ru.practicum.main.api.user.NewUserRequest;
import ru.practicum.main.api.user.UserDto;

import java.util.Collection;
import java.util.Set;

public interface UserService {

    UserDto create(NewUserRequest userDto);

    Collection<UserDto> getUsers(Set<Long> ids, int from, int size);

    void delete(long userId);
}
