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
public class ItemResponseDto {

    private Long id;

    private String description;

    private LocalDateTime created;

    private List<ItemDtoMarker> items;
}
