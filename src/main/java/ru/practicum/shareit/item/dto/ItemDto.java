package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Builder
@Data
public class ItemDto {

    private Long id;

    @NotBlank(groups = {Marker.Create.class})
    private String name;

    @NotBlank(groups = {Marker.Create.class})
    private String description;

    @NotNull(groups = {Marker.Create.class})
    private Boolean available;

    @Nullable
    private Long userId;

    @Nullable
    private BookingShortDto lastBooking;

    @Nullable
    private BookingShortDto nextBooking;

    @Nullable
    private List<CommentDto> comments;

}
