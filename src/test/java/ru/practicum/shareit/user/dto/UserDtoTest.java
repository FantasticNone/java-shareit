package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

class UserDtoTest {
    User user = User.builder()
            .id(1L)
            .name("alex")
            .email("alex@mail.ru")
            .build();

    @Test
    void of() {
        UserDto expectedDto;
        UserDto actualDto;

        expectedDto = UserDto.builder()
                .id(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .build();
        actualDto = UserMapper.toUserDto(user);

        Assertions.assertEquals(expectedDto, actualDto);
    }

}