package ru.practicum.shareit.booking.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class BookingDtoIn {

    @FutureOrPresent(groups = {Marker.Create.class})
    @NotNull(groups = {Marker.Create.class})
    private LocalDateTime start;

    @Future(groups = {Marker.Create.class})
    @NotNull(groups = {Marker.Create.class})
    private LocalDateTime end;

    @NotNull(groups = {Marker.Create.class})
    private Long itemId;
}