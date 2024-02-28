package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

class BookingMapperTest {

    private Booking booking;
    private BookingShortDto bookingShortDto;

    private BookingDto bookingDto;
    private Item item;
    private User booker;


    @BeforeEach
    public void data() {
        booker = User.builder()
                .name("name")
                .id(1L)
                .build();
        item = Item.builder()
                .available(true)
                .id(1L)
                .name("name")
                .description("desc")
                .build();
        booking = Booking.builder()
                .id(1L)
                .status(WAITING)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .build();

        bookingDto = BookingDto.builder()
                .id(booking.getId())
                .status(String.valueOf(booking.getStatus()))
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toItemDto(booking.getItem()))
                .booker(UserMapper.toUserDto(booking.getBooker()))
                .build();

        bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(booker.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(2))
                .itemId(item.getId())
                .build();

    }

    @Test
    void responseDtoOf() {
        BookingDto actual = BookingMapper.responseDtoOf(booking);

        assertEquals(actual.getId(), booking.getId());
        assertEquals(actual.getBooker(), UserMapper.toUserDto(booking.getBooker()));
        assertEquals(actual.getItem().getId(), booking.getItem().getId());
    }

    @Test
    void shortResponseDtoOf() {
        BookingShortDto actual = BookingMapper.shortResponseDtoOf(booking);

        assertEquals(actual.getId(), bookingShortDto.getId());
        assertEquals(actual.getBookerId(), booker.getId());
    }

    @Test
    void responseDtoListOf() {

        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        expectedList = List.of(bookingDto);
        actualList = BookingMapper.responseDtoListOf(List.of(booking));

        Assertions.assertEquals(expectedList, actualList);
    }
}
