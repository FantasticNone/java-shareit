package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import java.util.List;

@Builder
@Data
public class ItemDto {

    private Long id;

    private String name;

    private String description;

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
