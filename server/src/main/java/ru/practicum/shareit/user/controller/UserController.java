package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;


@RestController
@Slf4j
@Validated
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.info("Getting all users");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.info("Getting user by id: {}", userId);
        return userService.getById(userId);
    }

    @PostMapping()
    public UserDto createUser(@RequestBody UserDto userDto) {
        log.info("Creating user: {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        log.info("Updating user by id: {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.info("Deleting user by id : {}", userId);
        userService.delete(userId);
    }
}