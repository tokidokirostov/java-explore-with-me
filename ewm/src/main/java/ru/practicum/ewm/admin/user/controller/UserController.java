package ru.practicum.ewm.admin.user.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.user.dto.UserCreateDto;
import ru.practicum.ewm.admin.user.dto.UserDto;
import ru.practicum.ewm.admin.user.dto.UserMapper;
import ru.practicum.ewm.admin.user.service.UserService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/users")
@AllArgsConstructor
public class UserController {
    @Autowired
    private final UserService userService;

    //Добавление нового пользователя
    @PostMapping
    public UserDto addUser(@RequestBody UserCreateDto userCreateDto) {
        log.info("---> Получен запрос POST /admin/users user - {}", userCreateDto.toString());
        return userService.addUser(UserMapper.toUserDto(userCreateDto));
    }

    //Получение информации о пользователе
    @GetMapping("{id}")
    public UserDto getUserById(@PathVariable Long id) {
        log.info("---> Получен запрос GET admin/users/{}", id);
        return userService.getUserById(id);
    }

    //Получение информации о пользователях
    @GetMapping
    public List<UserDto> getAllUsers(@RequestParam(required = false) List<String> ids,
                                     @RequestParam(defaultValue = "0") String from,
                                     @RequestParam(defaultValue = "10") String size) {
        log.info("---> Получен запрос GET /admin/users");
        return userService.getAllUsers(ids, from, size);
    }

    //Удаление пользователя
    @DeleteMapping("{id}")
    public void deleteUser(@PathVariable() String id) {
        log.info("---> Получен запрос DELETE admin/users/{}", id);
        userService.deleteUser(id);
    }
}
