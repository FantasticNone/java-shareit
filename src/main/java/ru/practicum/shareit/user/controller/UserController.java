package ru.practicum.shareit.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.utils.Marker;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;


@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @GetMapping()
    public List<UserDto> getAllUsers() {
        log.debug("Getting all users");
        return userService.getAll();
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable long userId) {
        log.debug("Getting user by id: {}", userId);
        return userService.getById(userId);
    }

    @PostMapping()
    public UserDto createUser(@Validated(Marker.Create.class) @RequestBody UserDto userDto) {
        log.debug("Creating user: {}", userDto);
        return userService.create(userDto);
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @Validated(Marker.Update.class) @RequestBody UserDto userDto) {
        log.debug("Updating user by id: {}", userId);
        return userService.update(userId, userDto);
    }

    @DeleteMapping("/{userId}")
    public void deleteUser(@PathVariable long userId) {
        log.debug("Deleting user by id : {}", userId);
        userService.delete(userId);
    }
}