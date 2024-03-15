package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDtoMarker {
    private Long id;

    @NotBlank(groups = {Marker.Create.class})
    @Size(max = 30, message = "The name must be no more than 30 characters",
            groups = {Marker.Create.class, Marker.Update.class})
    private String name;

    @NotBlank(groups = {Marker.Create.class})
    @Size(max = 255, message = "The description must be no more than 255 characters",
            groups = {Marker.Create.class, Marker.Update.class})
    private String description;

    @NotNull(groups = {Marker.Create.class})
    private Boolean available;

    private Long requestId;
}