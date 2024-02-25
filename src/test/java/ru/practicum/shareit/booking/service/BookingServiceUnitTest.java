package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.mockito.junit.jupiter.MockitoExtension;

import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class BookingServiceUnitTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingServiceImpl bookingService;


    @Test
    public void testCreateBooking() {

        BookingRequestDto bookingRequestDto = createBookingRequestDto();
        User user = createUser();
        Item item = createItem();

        when(userRepository.findById(bookingRequestDto.getId())).thenReturn(Optional.of(user));
        when(itemRepository.findById(bookingRequestDto.getItemId())).thenReturn(Optional.of(item));
        when(item.getOwner()).thenReturn(createUser());

        when(bookingRepository.save(any(Booking.class))).thenReturn(createBooking());

        BookingDto result = bookingService.create(bookingRequestDto);

        assertNotNull(result);
    }

    private BookingRequestDto createBookingRequestDto() {
        return BookingRequestDto.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .itemId(1L)
                .build();
    }

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("John Doe")
                .email("john.doe@example.com")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .id(1L)
                .name("Sample Item")
                .owner(createUser())
                .available(true)
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusHours(2))
                .booker(createUser())
                .item(createItem())
                .status(BookingStatus.WAITING)
                .build();
    }

}
