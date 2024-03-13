package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.user.dto.mapper.UserMapper;

import java.time.LocalDateTime;
import java.util.List;

public class BookingDtoTest {
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
        BookingDto expectedDto;
        BookingDto actualDto;

        expectedDto = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .booker(UserMapper.toUserDto(user))
                .item(ItemMapper.toItemDto(item))
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        actualDto = BookingMapper.responseDtoOf(booking);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        BookingDto correctDto = BookingDto.builder()
                .id(1L)
                .status("WAITING")
                .booker(UserMapper.toUserDto(user))
                .item(ItemMapper.toItemDto(item))
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .build();

        Booking booking = Booking.builder()
                .id(1L)
                .booker(user)
                .item(item)
                .start(localDateTime.minusDays(1))
                .end(localDateTime.plusDays(1))
                .status(BookingStatus.WAITING)
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = BookingMapper.responseDtoListOf(List.of(booking, booking, booking));

        Assertions.assertEquals(expectedList, actualList);
    }
}
