package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Assertions;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class ItemServiceUnitTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private ItemRequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    ItemDto itemDtoWithBookings;
    Booking lastBooking;
    Booking nextBooking;

    User requester;
    User owner;
    Item itemSaved;

    Comment commentSaved;
    CommentDto commentDto;
    CommentRequestDto commentRequestDto;

    @Test
    public void testCreateItem() {
        ItemDtoMarker itemDto = ItemDtoMarker.builder()
                .id(1L)
                .name("Test Item")
                .description("Test Description")
                .available(true)
                .build();
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        given(userRepository.findById(userId)).willReturn(Optional.of(user));

        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        given(itemRepository.save(any(Item.class))).willReturn(item);

        ItemDtoMarker createdItem = itemService.create(itemDto, userId);

        verify(userRepository).findById(userId);
        verify(itemRepository).save(any(Item.class));

        assertEquals(itemDto.getName(), createdItem.getName());
        assertEquals(itemDto.getDescription(), createdItem.getDescription());
        assertTrue(createdItem.getAvailable());
    }


    @Test
    public void testUpdateItem() {
        Long itemId = 1L;
        ItemDtoMarker itemRequestDto = ItemDtoMarker.builder()
                .id(1L)
                .name("Updated Item Name")
                .description("Updated Description")
                .available(true)
                .build();
        long userId = 1L;

        User user = new User();
        user.setId(userId);

        Item item = Item.builder()
                .id(itemId)
                .name("Initial Item Name")
                .description("Initial Description")
                .available(true)
                .owner(user)
                .build();

        given(itemRepository.findById(itemId)).willReturn(Optional.of(item));
        given(itemRepository.save(any(Item.class))).willReturn(item);

        ItemDto updatedItem = itemService.update(itemId, itemRequestDto, userId);

        verify(itemRepository).findById(itemId);
        verify(itemRepository).save(any(Item.class));

        assertEquals(itemRequestDto.getName(), updatedItem.getName());
        assertEquals(itemRequestDto.getDescription(), updatedItem.getDescription());
        assertTrue(updatedItem.getAvailable());
    }

    @Test
    public void testItemDtoMarker() {
        Item item = new Item();
        ItemDtoMarker itemDtoMarker = ItemMapper.toItemDtoMarker(item);

        assertEquals(item.getId(), itemDtoMarker.getId());
        assertEquals(item.getName(), itemDtoMarker.getName());
        assertEquals(item.getDescription(), itemDtoMarker.getDescription());
        assertEquals(item.getAvailable(), itemDtoMarker.getAvailable());

    }

    @Test
    void getItemWithCommentsAndBookings() {

        owner = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();

        requester = User.builder()
                .id(2L)
                .name("user2")
                .email("email2@gmail.com")
                .build();

        itemSaved = Item.builder()
                .id(1L)
                .name("Shovel")
                .owner(owner)
                .description("Shovel for digging what is digging")
                .available(true)
                .build();

        lastBooking = Booking.builder()
                .id(1L)
                .start(LocalDateTime.now().minusDays(4))
                .end(LocalDateTime.now().minusDays(3))
                .booker(requester)
                .item(itemSaved)
                .status(BookingStatus.APPROVED)
                .build();

        nextBooking = Booking.builder()
                .id(2L)
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .booker(requester)
                .item(itemSaved)
                .status(BookingStatus.APPROVED)
                .build();

        itemDtoWithBookings = ItemDto.builder()
                .id(1L)
                .name("Shovel")
                .description("Shovel for digging what is digging")
                .userId(1L)
                .available(true)
                .build();
        ItemDto expectedDto;
        ItemDto actualDto;

        itemDtoWithBookings.setComments(Collections.emptyList());
        Mockito
                .when(itemRepository.findById(itemSaved.getId()))
                .thenReturn(Optional.ofNullable(itemSaved));
        Mockito
                .when(userRepository.findById(owner.getId()))
                .thenReturn(Optional.ofNullable(owner));
        Mockito
                .when(commentRepository.findAllByItemId(itemSaved.getId()))
                .thenReturn(Collections.emptyList());


        expectedDto = itemDtoWithBookings;
        actualDto = itemService.getItemById(itemSaved.getId(), owner.getId());

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    public void testGetItemsBySearch_WhenTextIsNotBlank_ReturnsItemList() {

        String searchText = "search text";
        Pageable page = PageRequest.of(0, 10);
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Test Item 1");
        item1.setDescription("Description 1");

        Item item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item 2");
        item2.setDescription("Description 2");

        List<Item> itemList = Arrays.asList(item1, item2);

        Mockito.when(itemRepository.findAllByAvailableTrueAndNameContainingIgnoreCaseOrAvailableTrueAndDescriptionContainingIgnoreCase(searchText, searchText, page)).thenReturn(itemList);

        List<ItemDto> result = itemService.getItemsBySearch(searchText, page);

        assertNotNull(result);
        assertEquals(2, result.size());

    }

    @Test
    public void testGetItemsBySearch_TextIsBlank_ReturnsEmptyList() {

        String searchText = "";
        Pageable page = PageRequest.of(0, 10);

        List<ItemDto> result = itemService.getItemsBySearch(searchText, page);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    void postComment() {

        User author = User.builder()
                .id(2L)
                .name("author")
                .email("author@gmail.com")
                .build();

        Item item = Item.builder()
                .id(1L)
                .name("Shovel")
                .owner(owner)
                .description("Shovel for digging what is digging")
                .available(true)
                .build();

        Comment comment = Comment.builder()
                .item(item)
                .author(author)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        commentSaved = Comment.builder()
                .id(1L)
                .item(item)
                .author(author)
                .text("comment text")
                .created(LocalDateTime.of(2000, 12, 12, 12, 12))
                .build();

        commentDto = CommentDto.builder()
                .id(1L)
                .text("comment text")
                .authorName(author.getName())
                .created(comment.getCreated())
                .build();

        commentRequestDto = CommentRequestDto.builder()
                .text("comment text")
                .build();


        CommentDto expectedComment;
        CommentDto actualComment;

        Mockito
                .when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(item));
        Mockito
                .when(userRepository.findById(anyLong()))
                .thenReturn(Optional.ofNullable(author));
        Mockito
                .when(bookingRepository.existsByItemAndBookerAndEndIsBefore(any(), any(), any(LocalDateTime.class)))
                .thenReturn(true);
        Mockito
                .when(commentRepository.save(any(Comment.class)))
                .thenReturn(commentSaved);

        expectedComment = commentDto;
        actualComment = itemService.postComment(author.getId(), item.getId(), commentRequestDto);

        Assertions.assertEquals(expectedComment, actualComment);
    }
}
