package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemDtoTest {

    LocalDateTime localDateTime = LocalDateTime.now();

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
    ItemDtoMarker itemDtoMarker = ItemDtoMarker.builder()
            .id(1L)
            .name("thing")
            .description("thing for smth")
            .available(true)
            .requestId(1L)
            .build();
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .created(localDateTime)
            .description("description")
            .requester(user)
            .build();

    @Test
    void toItemDto() {
        Item item = Item.builder()
                .id(1L)
                .owner(user)
                .name("drill")
                .description("drilling drill")
                .available(true)
                .build();

        List<Comment> comments = List.of(
                Comment.builder().id(1L).text("Comment 1").build(),
                Comment.builder().id(2L).text("Comment 2").build()
        );

        item.setComments(new ArrayList<>(comments));

        ItemDto expectedDto = ItemDto.builder()
                .id(1L)
                .userId(user.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(true)
                .lastBooking(null)
                .nextBooking(null)
                .comments(ItemMapper.mapCommentEntitiesToDtos(item.getComments())) // заменить на конкретную инициализацию
                .requestId(null) // Добавьте соответствующий ID, если требуется
                .build();

        ItemDto actualDto = ItemMapper.toItemDto(item);

        assertEquals(expectedDto, actualDto);
    }


    @Test
    void toItemDtoMarker() {
        ItemDtoMarker expectedDtoMarker;
        ItemDtoMarker actualDtoMarker;

        item.setRequest(itemRequest);

        expectedDtoMarker = ItemDtoMarker.builder()
                .id(1L)
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(itemRequest.getId())
                .build();

        actualDtoMarker = ItemMapper.toItemDtoMarker(item);

        assertEquals(expectedDtoMarker, actualDtoMarker);
    }

    @Test
    void toItem() {
        ItemDtoMarker itemDtoMarker = ItemDtoMarker.builder()
                .id(1L)
                .name("thing")
                .description("thing for smth")
                .available(true)
                .requestId(1L)
                .build();

        Item expectedItem = Item.builder()
                .id(itemDtoMarker.getId())
                .name(itemDtoMarker.getName())
                .description(itemDtoMarker.getDescription())
                .available(itemDtoMarker.getAvailable())
                .request(itemRequest)
                .build();

        Item actualItem = ItemMapper.toItem(itemDtoMarker);

        assertEquals(expectedItem.getName(), actualItem.getName());
    }
}
