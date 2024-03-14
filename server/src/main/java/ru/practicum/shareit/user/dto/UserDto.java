package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Builder
@Data
@EqualsAndHashCode
public class UserDto {

    private long id;

    private String name;

    private String email;
}