package ru.practicum.shareit.user.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;

import java.util.*;


@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {
    private final Map<Long, User> users = new HashMap<>();
    private static int userId = 0;

    @Override
    public User create(User user) {
        user.setId(++userId);
        users.put(user.getId(), user);
        log.debug("Creating user: {}", user);
        return user;
    }

    @Override
    public Optional<User> getById(long userId) {
        log.debug("Getting User by ID: {}", userId);
        return Optional.ofNullable(users.get(userId));
    }

    @Override
    public List<User> getAll() {
        log.debug("Getting all users");
        return new ArrayList<>(users.values());
    }

    @Override
    public void delete(long userId) {
        log.debug("Removing User with id : {}", userId);
        if (!users.containsKey(userId)) {
            throw new NotFoundException(String.format("Invalid user id ", User.class));
        }
        users.remove(userId);
    }
}