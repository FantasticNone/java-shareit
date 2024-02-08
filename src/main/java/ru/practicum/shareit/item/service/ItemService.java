package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemRequestDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    ItemDto getItemById(long itemId, long userId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> getItemsBySearch(String text);

    CommentDto postComment(Long userId, Long itemId, CommentRequestDto commentRequestDto);
}