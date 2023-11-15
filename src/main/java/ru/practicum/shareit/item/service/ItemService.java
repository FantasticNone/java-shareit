package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto itemDto, long userId);

    ItemDto update(long itemId, ItemDto itemDto, long userId);

    ItemDto getItemById(long itemId);

    List<ItemDto> getItemsByUserId(long userId);

    List<ItemDto> getItemsBySearch(String text);
}