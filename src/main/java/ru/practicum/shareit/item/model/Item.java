package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
public class Item {

    private long id;

    @NotBlank(groups = {Marker.Update.class})
    private String name;

    @NotBlank(groups = {Marker.Update.class})
    private String description;

    private Boolean available;

    private long owner;
}