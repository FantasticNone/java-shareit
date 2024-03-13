package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    UserDto create(UserDto userDto);

    UserDto getById(long id);

    List<UserDto> getAll();

    UserDto update(long userId, UserDto userdto);

    void delete(long id);
}