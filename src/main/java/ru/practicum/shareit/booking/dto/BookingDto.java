package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    @Null(groups = Marker.Update.class)
    private Long id;

    private Long itemId;
    private Long bookerId;

    @NotNull(groups = Marker.Create.class)
    @FutureOrPresent(groups = Marker.Create.class)
    private LocalDateTime start;

    @NotNull(groups = Marker.Create.class)
    @Future(groups = Marker.Create.class)
    private LocalDateTime end;

    @Null(groups = Marker.Create.class)
    private String status;
}
