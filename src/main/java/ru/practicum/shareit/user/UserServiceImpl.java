package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        log.debug("Creating user : {}", userDto.getName());
        User user = userRepository.save(UserMapper.toUser(userDto));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto getById(long userId) {
        log.debug("Getting user by Id: {}", userId);
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", User.class)));
        return UserMapper.toUserDto(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAll() {
        log.debug("Getting all users");
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public UserDto update(long userId, UserDto userDto) {
        log.debug("Updating user: {}", userDto.getName());
        User user = userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", User.class)));
        String name = userDto.getName();
        String email = userDto.getEmail();
        if (name != null && !name.isBlank()) {
            user.setName(name);
        }
        if (email != null && !email.isBlank()) {
            user.setEmail(email);
        }
        return UserMapper.toUserDto(user);
    }

    @Override
    public void delete(long id) {
        log.debug("Deleting user by id: {}", id);
        userRepository.deleteById(id);
    }
}