package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.valid.StartBeforeEndDateValid;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@StartBeforeEndDateValid
@EqualsAndHashCode
public class BookingRequestDto {
    private Long id;
    @FutureOrPresent
    private LocalDateTime start;
    private LocalDateTime end;
    @NotNull
    private Long itemId;
}