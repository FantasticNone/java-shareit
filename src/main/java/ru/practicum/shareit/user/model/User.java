package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class User {

    private long id;

    @NotBlank(groups = {Marker.Update.class})
    private String name;

    @NotBlank(groups = {Marker.Update.class})
    @Email(groups = {Marker.Update.class, Marker.Create.class})
    private String email;
}
