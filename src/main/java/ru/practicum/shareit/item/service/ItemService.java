package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;

import java.util.List;

public interface ItemService {
    ItemDtoOut getItemById(long itemId, long userId);

    List<ItemDtoOut> getItemsByUserId(long userId);

    List<ItemDtoOut> getItemsBySearch(String text);

    ItemDtoOut create(ItemDtoIn itemDtoIn, long userId);

    ItemDtoOut update(long itemId, ItemDtoIn itemDtoIn, long userId);

    CommentDtoOut saveNewComment(long itemId, CommentDtoIn commentDtoIn, long userId);
}
