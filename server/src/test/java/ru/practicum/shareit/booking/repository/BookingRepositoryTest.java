package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingRepositoryTest {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    User booker;

    User owner;
    User ownerTwo;

    Item item;
    Item itemTwo;

    Booking booking;
    Booking bookingTwo;

    @BeforeEach
    void setUp() {
        LocalDateTime localDateTime = LocalDateTime.now();

        booker = User.builder()
                .name("user")
                .email("email@gmail.com")
                .build();
        userRepository.save(booker);

        owner = User.builder()
                .name("user1")
                .email("email1@gmail.com")
                .build();
        ownerTwo = User.builder()
                .name("user2")
                .email("email2@gmail.com")
                .build();
        userRepository.save(owner);
        userRepository.save(ownerTwo);

        item = Item.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .owner(owner)
                .build();
        itemTwo = Item.builder()
                .name("shovel")
                .description("shovel for digging")
                .available(true)
                .owner(ownerTwo)
                .build();
        itemRepository.save(item);
        itemRepository.save(itemTwo);
        booking = Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.WAITING)
                .start(localDateTime.plusDays(5))
                .end(localDateTime.plusDays(10))
                .build();
        bookingTwo = Booking.builder()
                .booker(booker)
                .item(itemTwo)
                .status(BookingStatus.WAITING)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .build();
    }

    @Test
    void findAllByBooker() {
        List<Booking> expectedList;
        List<Booking> actualList;

        bookingRepository.save(booking);
        expectedList = List.of(booking);
        actualList = bookingRepository.findAllByBookerId(booker.getId(), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testExistsByItemAndBookerAndEndIsBefore() {

        boolean expected = false;
        bookingRepository.save(booking);

        boolean actual = bookingRepository.existsByItemAndBookerAndEndIsBefore(item, booker, LocalDateTime.now());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void testFindAllByOwnerItems() {

        List<Booking> expectedList = List.of(booking);
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByOwnerItems(Collections.singletonList(item.getId()), Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindAllByOwnerItemsAndWaitingStatus() {

        List<Booking> expectedList = List.of(booking);
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByOwnerItemsAndWaitingStatus(Collections.singletonList(item.getId()), BookingStatus.WAITING, Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindAllByOwnerItemsAndRejectedStatus() {

        List<BookingStatus> rejectedStatusList = List.of(BookingStatus.REJECTED);
        List<Booking> expectedList = List.of();
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByOwnerItemsAndRejectedStatus(Collections.singletonList(item.getId()), rejectedStatusList, Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindAllByOwnerItemsAndCurrentStatus() {

        LocalDateTime now = LocalDateTime.now();
        List<Booking> actualList = bookingRepository.findAllByOwnerItemsAndCurrentStatus(
                List.of(item.getId()),
                now,
                Pageable.unpaged()
        );

        Assertions.assertTrue(actualList.isEmpty());
    }

    @Test
    void testFindAllByOwnerItemsAndFutureStatus() {

        booking.setStart(LocalDateTime.now().plusDays(1));
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByOwnerItemsAndFutureStatus(List.of(item.getId()), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertTrue(!actualList.isEmpty());
    }

    @Test
    void testFindAllByOwnerItemsAndPastStatus() {

        booking.setEnd(LocalDateTime.now().minusDays(1));
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByOwnerItemsAndPastStatus(Collections.singletonList(item.getId()), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertFalse(actualList.isEmpty());
    }

    @Test
    void testFindAllByBookerIdAndWaitingStatus() {

        List<Booking> expectedList = List.of(booking);
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByBookerIdAndWaitingStatus(booker.getId(), BookingStatus.WAITING, Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindAllByBookerIdAndRejectedStatus() {

        List<BookingStatus> rejectedStatusList = List.of(BookingStatus.REJECTED);
        List<Booking> expectedList = List.of();
        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByBookerIdAndRejectedStatus(booker.getId(), rejectedStatusList, Pageable.unpaged());

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    void testFindAllByBookerIdAndCurrentStatus() {

        bookingRepository.save(booking);

        List<Booking> actualList = bookingRepository.findAllByBookerIdAndCurrentStatus(booker.getId(), LocalDateTime.now(), Pageable.unpaged());

        Assertions.assertTrue(actualList.isEmpty());
    }

    @Test
    void testFindAllByBookerIdAndFutureStatus() {
        LocalDateTime now = LocalDateTime.now();
        userRepository.save(booker);
        itemRepository.save(item);
        booking = bookingRepository.save(booking);

        List<Booking> resultList = bookingRepository.findAllByBookerIdAndFutureStatus(booker.getId(), now, Pageable.unpaged());

        Assertions.assertTrue(resultList.contains(booking));
    }

    @Test
    void testFindAllByBookerIdAndPastStatus() {
        LocalDateTime now = LocalDateTime.now();
        userRepository.save(booker);
        itemRepository.save(item);
        booking = bookingRepository.save(booking);

        List<Booking> resultList = bookingRepository.findAllByBookerIdAndPastStatus(booker.getId(), now, Pageable.unpaged());

        Assertions.assertFalse(resultList.contains(booking));
    }

    @Test
    void testFindAllByItem_IdIn() {
        itemRepository.save(item);
        booking = bookingRepository.save(booking);

        List<Booking> resultList = bookingRepository.findAllByItem_IdIn(Collections.singletonList(item.getId()), Sort.by(Sort.Direction.ASC, "id"));

        Assertions.assertTrue(resultList.contains(booking));
    }

    @Test
    void testFindApprovedByItems() {
        itemRepository.save(item);
        booking = Booking.builder()
                .booker(booker)
                .item(item)
                .status(BookingStatus.APPROVED)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        bookingRepository.save(booking);

        List<Booking> resultList = bookingRepository.findApprovedByItems(Collections.singletonList(item.getId()));

        Assertions.assertTrue(resultList.contains(booking));
    }
}


