package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.lang.Nullable;
import ru.practicum.shareit.booking.dto.BookingShortDto;

import javax.validation.constraints.Size;
import java.util.List;

@Builder
@Data
public class ItemDto {

    private Long id;

    @Size(max = 30, message = "The name must be no more than 30 characters")
    private String name;

    @Size(max = 255, message = "The description must be no more than 255 characters")
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
