package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    Item create(Item item, long userId);

    Optional<Item> getItemById(long itemId);

    List<Item> getItemsByUserId(long userId);

    List<Item> getItemsBySearch(String text);
}