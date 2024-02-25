package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {

    private Long id;

    @NotBlank(groups = {Marker.Create.class})
    @Size(max = 255, message = "The description must be no more than 255 characters",
            groups = {Marker.Create.class, Marker.Update.class})
    private String description;

    private LocalDateTime created;

    private List<ItemDtoMarker> items;
}
