package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> getItemsBySearch(String text);

    CommentDto postComment(Long userId, Long itemId, CommentDto commentRequestDto);
}