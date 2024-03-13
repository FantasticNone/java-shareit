package ru.practicum.shareit.booking.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
class BookingShortDtoTest {

    @Autowired
    JacksonTester<BookingShortDto> json;

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

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void testBookingShortDto_Json() throws Exception {
        BookingShortDto bookingShortDto = BookingShortDto.builder()
                .id(1L)
                .bookerId(user.getId())
                .build();

        JsonContent<BookingShortDto> result = json.write(bookingShortDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.bookerId").isEqualTo(1);
    }
}
