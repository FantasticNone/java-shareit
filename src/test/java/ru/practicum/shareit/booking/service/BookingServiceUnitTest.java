package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.exception.ItemBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.booking.BookingState.*;

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

    private static final Pageable PAGE_FOR_BOOKINGS = PageRequest.of(0, 10, Sort.by("start").descending());

    BookingRequestDto bookingRequestDto;

    User booker;
    User owner;
    User unauthorizedUser;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;
    Booking bookingSaved;
    Booking booking1;

    User userWithNoItems;

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

        unauthorizedUser = User.builder()
                .id(3L)
                .name("unauthorizedUser")
                .email("unauthorized@gmail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .name("thing")
                .owner(owner)
                .description("smth things 'bout thing")
                .available(true)
                .build();
        item2 = Item.builder()
                .id(2L)
                .name("thing")
                .owner(owner)
                .description("smth things 'bout thing")
                .available(true)
                .build();

        booking = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        booking2 = Booking.builder()
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item2)
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

        booking1 = Booking.builder()
                .id(3L)
                .start(bookingRequestDto.getStart())
                .end(bookingRequestDto.getEnd())
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .build();

        userWithNoItems = User.builder()
                .id(4L).name("userWithNoItems")
                .email("user4@gmail.com")
                .build();

    }

    @Test
    void create() {
        BookingDto expectedDto;
        BookingDto actualDto;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);


        expectedDto = BookingMapper.responseDtoOf(bookingSaved);
        actualDto = bookingService.create(bookingRequestDto);

        assertEquals(expectedDto.getId(), actualDto.getId());

        item.setOwner(booker);

        NotFoundException entityNotFoundException = assertThrows(NotFoundException.class,
                () -> bookingService.create(bookingRequestDto));

        assertEquals(entityNotFoundException.getMessage(), "Owner can't book his item");
    }

    @Test
    void create_whenItemIsNotAvailable() {

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.ofNullable(booker));
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.ofNullable(item));
        when(bookingRepository.save(booking))
                .thenReturn(bookingSaved);


        BookingMapper.responseDtoOf(bookingSaved);
        bookingService.create(bookingRequestDto);

        item.setAvailable(false);

        ItemBookingException incorrectItemBookingException = assertThrows(ItemBookingException.class,
                () -> bookingService.create(bookingRequestDto));

        assertEquals(incorrectItemBookingException.getMessage(), "Item is unavailable");
    }

    @Test
    void userHasNoRightsToApproveBooking() {
        String expectedMessage = "User id not found";
        String actualMessage = null;

        User unauthorizedUser = User.builder()
                .id(3L)
                .name("unauthorizedUser")
                .email("unauthorized@gmail.com")
                .build();

        Exception exception = assertThrows(NotFoundException.class,
                () -> bookingService.approve(1L, unauthorizedUser.getId(), true));
        actualMessage = exception.getMessage();

        assertEquals(expectedMessage, actualMessage);
    }

    @Test
    void approveBooking() {
        BookingDto expectedResponse;
        BookingDto actualResponse;

        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findById(3L)).thenReturn(Optional.of(booking1));

        actualResponse = bookingService.approve(3L, owner.getId(), true);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setId(3L);
        expectedResponse = BookingMapper.responseDtoOf(booking);

        Assertions.assertEquals(expectedResponse, actualResponse);
    }

    /*@Test
    void approveBooking_whenUserAndItemExistAndUserIsNotOwner() {
        User someOtherUser = User.builder()
                .id(5L)
                .name("someOtherUser")
                .email("someOtherUser@gmail.com")
                .build();

        when(userRepository.existsById(owner.getId())).thenReturn(true);
        when(bookingRepository.findById(3L)).thenReturn(Optional.of(booking1));

        NotFoundException exception = assertThrows(NotFoundException.class,
                    () -> bookingService.approve(3L, owner.getId(), true));

            assertEquals("User has no rights to approve booking", exception.getMessage());
    }*/

    @Test
    void getBookingForUserWhenUserIsBooker() {

        when(bookingRepository.findById(bookingSaved.getId())).thenReturn(Optional.of(bookingSaved));

        BookingDto actualBooking = bookingService.getBooking(bookingSaved.getId(), booker.getId());

        assertEquals(bookingSaved.getId(), actualBooking.getId());
        assertEquals(booker.getId(), actualBooking.getBooker().getId());
    }

    @Test
    void getAllByUserReturnsBookings() {
        List<Booking> expectedBookings = Arrays.asList(bookingSaved, booking1);
        List<BookingDto> actualResponse;

        when(userRepository.findById(booker.getId())).thenReturn(Optional.ofNullable(booker));
        when(bookingRepository.findAllByBookerId(booker.getId(), PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start")))).thenReturn(expectedBookings);

        actualResponse = bookingService.getAllByUser(booker.getId(), BookingState.ALL, PageRequest.of(0, 10));

        Assertions.assertEquals(expectedBookings.size(), actualResponse.size());
    }

    @Test
    void getAllByOwnerWhenUserHasMoreThanOneItem() {

        User userWithMultipleItems = User.builder()
                .id(5L)
                .name("userWithMultipleItems")
                .email("user@gmail.com").build();
        List<Item> userItems = Arrays.asList(
                Item.builder()
                        .id(2L)
                        .name("item2")
                        .owner(userWithMultipleItems)
                        .description("some item 2")
                        .available(true).build()
        );

        when(userRepository.findById(userWithMultipleItems.getId()))
                .thenReturn(Optional.of(userWithMultipleItems));

        when(itemRepository.findByOwnerIdWithoutPageable(userWithMultipleItems.getId()))
                .thenReturn(userItems);

        when(bookingRepository.findAllByOwnerItems(
                Mockito.eq(userItems.stream().map(Item::getId).collect(Collectors.toList())),
                Mockito.any(Pageable.class)
        )).thenReturn(Collections.singletonList(booking));

        List<BookingDto> actualResponse = bookingService.getAllByOwner(userWithMultipleItems.getId(), BookingState.ALL, PageRequest.of(0, 10));

        assertNotNull(actualResponse);
        Assertions.assertEquals(userItems.size(), actualResponse.size());
    }

    @Test
    void getAllByOwnerWhenUserHasNoItems() {
        List<Item> userItems = Collections.emptyList();

        when(userRepository.findById(userWithNoItems.getId()))
                .thenReturn(Optional.of(userWithNoItems));
        when(itemRepository.findByOwnerIdWithoutPageable(userWithNoItems.getId()))
                .thenReturn(userItems);

        DataException exception = assertThrows(DataException.class, () -> bookingService.getAllByOwner(userWithNoItems.getId(), BookingState.ALL, PageRequest.of(0, 10)));
        assertEquals("This method only for users who have >1 items", exception.getMessage());
    }

    @Test
    void getCurrentUserBookings() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndCurrentStatus(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingSaved));
        bookingSaved.setBooker(booker);

        expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved));
        actualList = bookingService.getAllByUser(booker.getId(), CURRENT, PAGE_FOR_BOOKINGS);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getCurrentOwnerBookings() {
        List<Long> userItems = List.of(item.getId(), item2.getId());

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdWithoutPageable(owner.getId()))
                .thenReturn(List.of(item, item2));
        when(bookingRepository.findAllByOwnerItemsAndCurrentStatus(eq(userItems), any(LocalDateTime.class), eq(PAGE_FOR_BOOKINGS)))
                .thenReturn(List.of(bookingSaved, booking2));

        List<BookingDto> actualList = bookingService.getAllByOwner(owner.getId(), BookingState.CURRENT, PAGE_FOR_BOOKINGS);

        List<BookingDto> expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved, booking2));

        assertEquals(expectedList, actualList);
    }

    @Test
    void getFutureUserBookings() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndFutureStatus(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingSaved));
        bookingSaved.setBooker(booker);

        expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved));
        actualList = bookingService.getAllByUser(booker.getId(), FUTURE, PAGE_FOR_BOOKINGS);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getFutureOwnerBookings() {
        List<Long> userItems = List.of(item.getId(), item2.getId());

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdWithoutPageable(owner.getId()))
                .thenReturn(List.of(item, item2));
        when(bookingRepository.findAllByOwnerItemsAndFutureStatus(eq(userItems), any(LocalDateTime.class), eq(PAGE_FOR_BOOKINGS)))
                .thenReturn(List.of(bookingSaved, booking2));

        List<BookingDto> actualList = bookingService.getAllByOwner(owner.getId(), FUTURE, PAGE_FOR_BOOKINGS);

        List<BookingDto> expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved, booking2));

        assertEquals(expectedList, actualList);
    }

    @Test
    void getPastUserBookings() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndPastStatus(anyLong(), any(LocalDateTime.class), any(Pageable.class)))
                .thenReturn(List.of(bookingSaved));
        bookingSaved.setBooker(booker);

        expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved));
        actualList = bookingService.getAllByUser(booker.getId(), PAST, PAGE_FOR_BOOKINGS);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getPastOwnerBookings() {
        List<Long> userItems = List.of(item.getId(), item2.getId());

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdWithoutPageable(owner.getId()))
                .thenReturn(List.of(item, item2));
        when(bookingRepository.findAllByOwnerItemsAndPastStatus(eq(userItems), any(LocalDateTime.class), eq(PAGE_FOR_BOOKINGS)))
                .thenReturn(List.of(bookingSaved, booking2));

        List<BookingDto> actualList = bookingService.getAllByOwner(owner.getId(), PAST, PAGE_FOR_BOOKINGS);

        List<BookingDto> expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved, booking2));

        assertEquals(expectedList, actualList);
    }

    @Test
    void getWaitingUserBookings() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndWaitingStatus(anyLong(), any(BookingStatus.class), any(Pageable.class)))
                .thenReturn(List.of(bookingSaved));
        bookingSaved.setBooker(booker);

        expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved));
        actualList = bookingService.getAllByUser(booker.getId(), WAITING, PAGE_FOR_BOOKINGS);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getWaitingOwnerBookings() {
        List<Long> userItems = List.of(item.getId(), item2.getId());

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdWithoutPageable(owner.getId()))
                .thenReturn(List.of(item, item2));
        when(bookingRepository.findAllByOwnerItemsAndWaitingStatus(eq(userItems), any(BookingStatus.class), eq(PAGE_FOR_BOOKINGS)))
                .thenReturn(List.of(bookingSaved, booking2));

        List<BookingDto> actualList = bookingService.getAllByOwner(owner.getId(), WAITING, PAGE_FOR_BOOKINGS);

        List<BookingDto> expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved, booking2));

        assertEquals(expectedList, actualList);
    }

    @Test
    void getRejectedUserBookings() {
        List<BookingDto> expectedList;
        List<BookingDto> actualList;

        when(userRepository.findById(booker.getId()))
                .thenReturn(Optional.of(booker));
        when(bookingRepository.findAllByBookerIdAndRejectedStatus(anyLong(), anyList(), any(Pageable.class)))
                .thenReturn(List.of(bookingSaved));
        bookingSaved.setBooker(booker);

        expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved));
        actualList = bookingService.getAllByUser(booker.getId(), REJECTED, PAGE_FOR_BOOKINGS);
        assertEquals(expectedList, actualList);
    }

    @Test
    void getRejectedOwnerBookings() {
        List<Long> userItems = List.of(item.getId(), item2.getId());

        when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.of(owner));
        when(itemRepository.findByOwnerIdWithoutPageable(owner.getId()))
                .thenReturn(List.of(item, item2));
        when(bookingRepository.findAllByOwnerItemsAndRejectedStatus(eq(userItems), anyList(), eq(PAGE_FOR_BOOKINGS)))
                .thenReturn(List.of(bookingSaved, booking2));

        List<BookingDto> actualList = bookingService.getAllByOwner(owner.getId(), REJECTED, PAGE_FOR_BOOKINGS);

        List<BookingDto> expectedList = BookingMapper.responseDtoListOf(List.of(bookingSaved, booking2));

        assertEquals(expectedList, actualList);
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
