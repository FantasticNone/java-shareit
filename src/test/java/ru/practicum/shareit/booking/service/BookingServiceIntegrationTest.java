package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceIntegrationTest {
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingService bookingService;


    Item item;
    User owner;
    User booker;
    Booking booking;
    LocalDateTime localDateTime;
    BookingRequestDto bookingRequestDto;


    @BeforeEach
    void setUp() {
        owner = User.builder()
                .name("user")
                .email("email@gmail.com")
                .build();

        booker = User.builder()
                .name("user2")
                .email("email2@gmail.com")
                .build();

        item = Item.builder()
                .name("drill")
                .description("drilling drill")
                .owner(owner)
                .available(true)
                .build();

        booking = Booking.builder()
                .item(item)
                .booker(booker)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();

        localDateTime = LocalDateTime.now();

        bookingRequestDto = BookingRequestDto.builder()
                .itemId(1L)
                .id(2L)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();
    }

    @Test
    void create() {
        userRepository.save(owner);
        UserDto bookerDto = UserMapper.toUserDto(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.save(item));

        BookingDto createdBooking = bookingService.create(bookingRequestDto);

        assertNotNull(createdBooking.getId());
        assertEquals(itemDto.getId(), createdBooking.getItem().getId());
        assertEquals(bookerDto.getId(), createdBooking.getBooker().getId());
    }

    @Test
    void approve() {
        UserDto ownerDto = UserMapper.toUserDto(userRepository.save(owner));
        UserDto bookerDto = UserMapper.toUserDto(userRepository.save(booker));
        ItemDto itemDto = ItemMapper.toItemDto(itemRepository.save(item));
        BookingDto createdBooking = bookingService.create(bookingRequestDto);

        BookingDto acceptedBookingResponseDto = bookingService.approve(
                ownerDto.getId(),
                createdBooking.getId(),
                true);

        Assertions.assertEquals("APPROVED", acceptedBookingResponseDto.getStatus());
    }
}