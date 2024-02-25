package ru.practicum.shareit.request.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ItemRequestMapperTest {

    @Test
    void testToItemRequest() {
        ItemResponseDto responseDto = new ItemResponseDto(1L, "Test Description");

        ItemRequest itemRequest = ItemRequestMapper.toItemRequest(responseDto);

        assertEquals("Test Description", itemRequest.getDescription());
    }

    @Test
    void testToItemRequestDto() {
        ItemRequest itemRequest = new ItemRequest(1L, "Test Description", null, LocalDateTime.now());

        ItemRequestDto requestDto = ItemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(1L, requestDto.getId());
        assertEquals("Test Description", requestDto.getDescription());
    }
}
