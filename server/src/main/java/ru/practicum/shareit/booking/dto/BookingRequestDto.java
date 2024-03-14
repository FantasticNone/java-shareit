package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@Builder
@EqualsAndHashCode
public class BookingRequestDto {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Long itemId;
}