package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceIntegrationTest {
    private final ItemService itemService;

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemRequestRepository requestRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    ItemRequest request;
    ItemDto itemDto;
    User owner;
    User requester;

    LocalDateTime localDateTime;

    @BeforeEach
    void setUp() {
        localDateTime = LocalDateTime.now();

        owner = User.builder()
                .name("user")
                .email("email@gmail.com")
                .build();

        requester = User.builder()
                .name("user2")
                .email("email2@gmail.com")
                .build();

        request = ItemRequest.builder()
                .description("test description")
                .created(localDateTime)
                .requester(requester)
                .build();

        itemDto = ItemDto.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

    }

    @Test
    void addItem() {
        userRepository.save(owner);
        userRepository.save(requester);
        requestRepository.save(request);

        ItemDtoMarker itemDtoMarker = ItemDtoMarker.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

        ItemDtoMarker actualItemDto = itemService.create(itemDtoMarker, owner.getId());

        Assertions.assertNotNull(actualItemDto.getId());
        Assertions.assertEquals(itemDtoMarker.getName(), actualItemDto.getName());
        Assertions.assertEquals(itemDtoMarker.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(Boolean.TRUE, actualItemDto.getAvailable());
        Assertions.assertEquals(itemDtoMarker.getRequestId(), actualItemDto.getRequestId());
    }

    @Test
    void updateItem() {
        User ownerSaved = userRepository.save(owner);

        Item trgItem = Item.builder()
                .name("shovel")
                .description("shoveling")
                .owner(ownerSaved)
                .available(true)
                .build();
        Long trgItemId = itemRepository.save(trgItem).getId();

        ItemDtoMarker updatedItemDtoMarker = ItemDtoMarker.builder()
                .id(trgItemId)
                .name("new shovel name")
                .description("updated shovel description")
                .available(false)
                .requestId(123L)
                .build();

        ItemDto actualItemDto = itemService.update(trgItemId, updatedItemDtoMarker, ownerSaved.getId());
        actualItemDto.setRequestId(updatedItemDtoMarker.getRequestId());

        Assertions.assertNotNull(actualItemDto.getId());
        Assertions.assertEquals(ownerSaved.getId(), actualItemDto.getUserId());
        Assertions.assertEquals(updatedItemDtoMarker.getName(), actualItemDto.getName());
        Assertions.assertEquals(updatedItemDtoMarker.getDescription(), actualItemDto.getDescription());
        Assertions.assertEquals(updatedItemDtoMarker.getAvailable(), actualItemDto.getAvailable());
        Assertions.assertEquals(updatedItemDtoMarker.getRequestId(), actualItemDto.getRequestId());
    }


    @Test
    void getItem() {
        ItemDto actualItemDto;

        User ownerSaved = userRepository.save(owner);
        User commenter = userRepository.save(requester);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(ownerSaved)
                .build();

        Item itemSaved = itemRepository.save(item);

        Comment comment = Comment.builder()
                .item(itemSaved)
                .author(commenter)
                .text("commenting item")
                .created(localDateTime)
                .build();

        Comment commentSaved = commentRepository.save(comment);

        actualItemDto = itemService.getItemById(itemSaved.getId(), ownerSaved.getId());

        Assertions.assertEquals(itemSaved.getId(), actualItemDto.getId());
        Assertions.assertEquals(itemSaved.getName(), actualItemDto.getName());
        Assertions.assertEquals(CommentMapper.listOfComments(List.of(commentSaved)), actualItemDto.getComments());
    }

    @Test
    void getUserItems() {
        List<ItemDto> actualItemDtoList;

        User ownerSaved = userRepository.save(owner);
        User bookerSaved = userRepository.save(requester);
        Item itemOne = Item.builder()
                .name("drill")
                .description("drilling drill")
                .owner(ownerSaved)
                .available(true)
                .build();
        Item itemTwo = Item.builder()
                .name("shovel")
                .description("shovelling shovel")
                .owner(ownerSaved)
                .available(true)
                .build();
        Item itemOneSaved = itemRepository.save(itemOne);
        Item itemTwoSaved = itemRepository.save(itemTwo);

        Booking lastBookingOne = Booking.builder()
                .item(itemOneSaved)
                .start(localDateTime.minusDays(10))
                .end(localDateTime.minusDays(9))
                .status(BookingStatus.APPROVED)
                .booker(bookerSaved)
                .build();
        Booking lastBookingTwo = Booking.builder()
                .item(itemTwoSaved)
                .start(localDateTime.minusDays(2))
                .end(localDateTime.minusDays(1))
                .status(BookingStatus.APPROVED)
                .booker(bookerSaved)
                .build();
        Booking nextBookingOne = Booking.builder()
                .item(itemOneSaved)
                .start(localDateTime.plusDays(9))
                .end(localDateTime.plusDays(10))
                .status(BookingStatus.APPROVED)
                .booker(bookerSaved)
                .build();
        Booking nextBookingTwo = Booking.builder()
                .item(itemTwoSaved)
                .start(localDateTime.plusDays(1))
                .end(localDateTime.plusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(bookerSaved)
                .build();

        BookingShortDto lastBookingOneDto = BookingMapper.shortResponseDtoOfTest(bookingRepository.save(lastBookingOne));
        BookingShortDto lastBookingTwoDto = BookingMapper.shortResponseDtoOfTest(bookingRepository.save(lastBookingTwo));
        BookingShortDto nextBookingOneDto = BookingMapper.shortResponseDtoOfTest(bookingRepository.save(nextBookingOne));
        BookingShortDto nextBookingTwoDto = BookingMapper.shortResponseDtoOfTest(bookingRepository.save(nextBookingTwo));

        Pageable page = PageRequest.of(0, 10);
        actualItemDtoList = itemService.getItemsByUserId(ownerSaved.getId(), page);

        Assertions.assertEquals(lastBookingOneDto, actualItemDtoList.get(0).getLastBooking());
        Assertions.assertEquals(lastBookingTwoDto, actualItemDtoList.get(1).getLastBooking());
        Assertions.assertEquals(nextBookingOneDto, actualItemDtoList.get(0).getNextBooking());
        Assertions.assertEquals(nextBookingTwoDto, actualItemDtoList.get(1).getNextBooking());
    }

    @Test
    void findItem() {
        List<ItemDto> expectedDtoList;
        List<ItemDto> actualDtoList;

        User ownerSaved = userRepository.save(owner);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(ownerSaved)
                .build();

        Item itemSaved = itemRepository.save(item);

        Pageable page = PageRequest.of(0, 10);

        expectedDtoList = List.of(ItemMapper.toItemDto(itemSaved));
        actualDtoList = itemService.getItemsBySearch("drill", page);

        Assertions.assertEquals(expectedDtoList, actualDtoList);
    }

    @Test
    void postComment() {
        CommentDto actualCommentDto;

        User ownerSaved = userRepository.save(owner);
        User bookerSaved = userRepository.save(requester);

        Item item = Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(ownerSaved)
                .build();
        Item itemSaved = itemRepository.save(item);

        Booking booking = Booking.builder()
                .item(itemSaved)
                .start(localDateTime.minusDays(4))
                .end(localDateTime.minusDays(2))
                .status(BookingStatus.APPROVED)
                .booker(bookerSaved)
                .build();
        Booking bookingSaved = bookingRepository.save(booking);

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text text")
                .build();

        actualCommentDto = itemService.postComment(bookerSaved.getId(), itemSaved.getId(), commentRequestDto);

        Assertions.assertEquals(1L, actualCommentDto.getId());
        Assertions.assertEquals(commentRequestDto.getText(), actualCommentDto.getText());
        Assertions.assertEquals(bookerSaved.getName(), actualCommentDto.getAuthorName());
    }
}