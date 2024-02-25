package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ItemBookingException;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingStatus.WAITING;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

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
