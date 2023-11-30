package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class ItemDtoIn {
    @NotBlank(groups = {Marker.Create.class})
    @Size(max = 255, groups = {Marker.Create.class, Marker.Update.class})
    private String name;

    @NotBlank(groups = {Marker.Create.class})
    @Size(max = 1000, groups = {Marker.Create.class, Marker.Update.class})
    private String description;

    @NotNull(groups = {Marker.Create.class})
    private Boolean available;
}