package ru.practicum.server.service.impl;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main.api.user.NewUserRequest;
import ru.practicum.main.api.user.UserDto;
import ru.practicum.server.entity.QUser;
import ru.practicum.server.mapper.UserMapper;
import ru.practicum.server.repository.UserRepository;
import ru.practicum.server.service.UserService;
import ru.practicum.main.util.exception.AlreadyExistsException;
import ru.practicum.main.util.exception.NotFoundException;
import ru.practicum.pageable.OffsetBasedPageRequest;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.practicum.constants.Constants.SORT_BY_ID_ASC;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public Collection<UserDto> getUsers(Set<Long> ids, int from, int size) {
        BooleanBuilder builder = new BooleanBuilder();

        if (ids != null) {
            builder.and(QUser.user.id.in(ids));
        }

        final Pageable pageable = new OffsetBasedPageRequest(from, size, SORT_BY_ID_ASC);
        return userRepository.findAll(builder, pageable)
                .getContent()
                .stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserDto create(NewUserRequest userDto) {
        try {
            return userMapper.toUserDto(userRepository.save(userMapper.toUser(userDto)));
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistsException("User with email " + userDto.getEmail() + " already exists.");
        }
    }

    @Override
    @Transactional
    public void delete(long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new NotFoundException("User with ID = " + userId + " does not exists")
        );

        userRepository.deleteById(userId);
    }
}
