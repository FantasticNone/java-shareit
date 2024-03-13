package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

class ItemRequestMapperTest {
    LocalDateTime localDateTime = LocalDateTime.now();
    User user = User.builder()
            .id(1L)
            .name("user")
            .email("user@mail.ru")
            .build();
    Item item = Item.builder()
            .id(1L)
            .owner(user)
            .name("drill")
            .description("drilling drill")
            .available(true)
            .build();
    ItemRequest itemRequest = ItemRequest.builder()
            .id(1L)
            .description("test description")
            .requester(user)
            .created(localDateTime)
            .build();

    ItemRequestDto itemRequestDto = ItemRequestDto.builder()
            .id(1L)
            .description(itemRequest.getDescription())
            .build();

    @Test
    void toItemRequest() {
        ItemRequest expected;
        ItemRequest actual;

        expected = ItemRequest.builder()
                .id(itemRequestDto.getId())
                .description(itemRequestDto.getDescription())
                .build();

        actual = ItemRequestMapper.toItemRequest(itemRequestDto);

        Assertions.assertEquals(expected.getDescription(), actual.getDescription()

        );
    }

    @Test
    void toItemRequestDto() {
        ItemResponseDto expectedDto;
        ItemResponseDto actualDto;

        expectedDto = ItemResponseDto.builder()
                .id(itemRequest.getId())
                .items(List.of())
                .description(itemRequest.getDescription())
                .created(localDateTime)
                .build();

        actualDto = ItemRequestMapper.toItemResponseDto(itemRequest);

        Assertions.assertEquals(expectedDto, actualDto);
    }

    @Test
    void requestListToDto() {
        List<ItemResponseDto> expectedList;
        List<ItemResponseDto> actualList;

        ItemResponseDto correctDto = ItemResponseDto.builder()
                .id(itemRequest.getId())
                .items(List.of())
                .description(itemRequest.getDescription())
                .created(localDateTime)
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = ItemRequestMapper.requestListToDto(List.of(itemRequest, itemRequest, itemRequest));

        Assertions.assertEquals(expectedList, actualList);
    }
}