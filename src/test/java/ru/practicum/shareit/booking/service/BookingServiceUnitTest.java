package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.dao.DataIntegrityViolationException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {
    @InjectMocks
    BookingServiceImpl bookingService;
    @Mock
    BookingRepository bookingRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;

    BookingRequestDto bookingRequestDto;

    User booker;
    User owner;
    Item item;
    Booking booking;
    Booking bookingSaved;


    @BeforeEach
    public void init() {
        bookingRequestDto = BookingRequestDto.builder()
                .id(1L)
                .itemId(1L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .build();

        booker = User.builder()
                .id(1L)
                .name("booker")
                .email("booker@gmail.com")
                .build();

        owner = User.builder()
                .id(2L)
                .name("user")
                .email("user@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("thing")
                .owner(owner)
                .description("smth things 'bout thing")
                .available(true)
                .build();

        booking = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        bookingSaved = Booking.builder()
                .id(1L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();
    }

    /*@Test
    void create() {
        BookingDto expectedDto;
        BookingDto actualDto;

        Mockito
                .when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        Mockito
                .when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);


        expectedDto = BookingMapper.responseDtoOf(bookingSaved);
        actualDto = bookingService.create(bookingRequestDto);

        Assertions.assertEquals(expectedDto.getId(), actualDto.getId());

    }

    private Booking getBooking() {
        Booking booking = BookingMapper.toBooking(bookingRequestDto);
        booking.setId(1L);
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(BookingStatus.APPROVED);
        return booking;
    }*/

    @Test
    void bookItemAlreadyBooked() {
        String expectedMessage = "Item id 1 already booked";
        String actualMessage;

        when(userRepository.findById(eq(owner.getId()))).thenReturn(Optional.of(owner));
        when(itemRepository.findById(eq(item.getId()))).thenReturn(Optional.of(item));
        //when(bookingRepository.save(any(Booking.class).thenThrow(new DataIntegrityViolationException("Item already booked"));

        bookingService.create(bookingRequestDto);

        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingRequestDto));
        actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }





   /* @Test
    public void create_InvalidBookingRequestDto_ThrowsException() {

    }

    @Test
    public void approve_ValidBookingIdAndUserAndApproval_StatusUpdated() {
        given(userRepository.existsById(1L)).willReturn(true);
        given(bookingRepository.findById(1L)).willReturn(Optional.of(validBooking));
        given(validBooking.getItem().getOwner().getId()).willReturn(1L);
        given(validBooking.getItem().getAvailable()).willReturn(true);
        given(bookingRepository.save(any(Booking.class))).willReturn(validBooking);

        BookingDto result = bookingService.approve(1L, 1L, true);

        assertNotNull(result);
        assertEquals(validBooking.getStatus(), BookingStatus.APPROVED);

    }

    @Test
    public void approve_InvalidBookingId_ThrowsException() {

    }*/

}
