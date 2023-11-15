package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailIsAlreadyRegisteredException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Creating user : {}", userDto);
        checkingUniqueEmail(userDto);
        User user = userRepository.create(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Override
    public UserDto getById(long userId) {
        log.debug("Getting user by Id: {}", userId);
        User user = userRepository.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", User.class)));
        return UserMapper.toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        log.debug("Getting all users");
        return userRepository.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        User user = userRepository.getById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", User.class)));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            if (!user.getEmail().equals(userDto.getEmail())) {
                checkingUniqueEmail(userDto);
            }
            user.setEmail(email);
        }
        log.debug("Updating user: {}", user);
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        log.debug("Deleting user by id: {}", id);
        userRepository.delete(id);
    }

    private void checkingUniqueEmail(UserDto userDto) {
        if (userRepository.getAll().stream().anyMatch(user -> user.getEmail().equals(userDto.getEmail()))) {
            throw new EmailIsAlreadyRegisteredException(String.format("Email %s is already registered ", userDto.getEmail()));
        }
    }
}