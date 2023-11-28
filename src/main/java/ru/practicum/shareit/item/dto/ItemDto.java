package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Builder
@Data
@AllArgsConstructor
public class ItemDto {

    private final Long id;

    @NotBlank(groups = {Marker.Create.class})
    private final String name;

    @NotBlank(groups = {Marker.Create.class})
    private final String description;

    @NotNull(groups = {Marker.Create.class})
    private final Boolean available;
}
