package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.dto.NewUserRequest;
import ru.practicum.ewm.user.dto.UserDto;
import ru.practicum.ewm.user.service.UserService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.util.List;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminUserController {
    private final UserService userService;
    private final ValidationUtil validation;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UserDto addUser(@Valid @RequestBody NewUserRequest dto) {
        log.info("Получен запрос на создание нового пользователя: {}", dto);
        validation.validationForAddUser(dto);
        return userService.addUser(dto);
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUserById(@PathVariable Long userId) {
        log.info("Поступил запрос на удаление пользователя с id - {}", userId);
        validation.checkUserId(userId);
        userService.deleteUserById(userId);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0")
                                  @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                  @RequestParam(defaultValue = "10")
                                  @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Поступил запрос на получение списка пользователей - {} с отступом - {} и количеством элементов - {}",
                ids, from, size);
        return userService.getUsers(ids, from, size);
    }
}
