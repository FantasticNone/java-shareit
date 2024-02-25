package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

class BookingShortDtoTest {
    User user = User.builder()
            .id(1L)
            .name("user")
            .email("email@mail.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .owner(user)
            .name("drill")
            .description("drilling drill")
            .available(true)
            .build();

    LocalDateTime localDateTime = LocalDateTime.now();

    @Test
    void of() {
        BookingShortDto expectedDto;
        BookingShortDto actualDto;

        expectedDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(user.getId())
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        actualDto = BookingMapper.shortResponseDtoOf(booking);

        Assertions.assertEquals(expectedDto, actualDto);
    }
}
