package ru.practicum.shareit.item.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;

import java.util.List;

public interface ItemService {

    ItemDtoMarker create(ItemDtoMarker itemDto, long userId);

    ItemDto update(long itemId, ItemDtoMarker itemRequestDto, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(Long userId, Pageable page);

    List<ItemDto> getItemsBySearch(String text, Pageable page);

    CommentDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}