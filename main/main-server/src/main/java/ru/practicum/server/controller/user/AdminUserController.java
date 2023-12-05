package ru.practicum.server.controller.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.main.api.user.NewUserRequest;
import ru.practicum.main.api.user.UserDto;
import ru.practicum.server.service.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.Collection;
import java.util.Set;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/users")
@Validated
public class AdminUserController {
    private final UserService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserRequest userDto) {
        return service.create(userDto);
    }

    @GetMapping
    public Collection<UserDto> getUsers(@RequestParam(required = false) Set<Long> ids,
                                        @RequestParam(defaultValue = "0") @Min(0) int from,
                                        @RequestParam(defaultValue = "10") @Min(1) int size) {
        return service.getUsers(ids, from, size);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Min(1) long userId) {
        service.delete(userId);
    }
}
