package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
@Slf4j
@RequiredArgsConstructor
public class ItemRepositoryImpl implements ItemRepository {
    private final Map<Long, Item> items = new HashMap<>();
    private final Map<Long, List<Item>> userItemIndex = new LinkedHashMap<>();
    private int itemId = 0;


    @Override
    public Item create(Item item, long userId) {
        item.setId(++itemId);
        item.setOwner(userId);
        items.put(item.getId(), item);
        final List<Item> itemsByOwner = userItemIndex.computeIfAbsent(item.getOwner(), k -> new ArrayList<>());
        itemsByOwner.add(item);
        log.debug("Adding item: {}", item);
        return item;
    }

    @Override
    public Optional<Item> getItemById(long itemId) {
        log.debug("Getting item by id: {} ", itemId);
        return Optional.ofNullable(items.get(itemId));
    }

    @Override
    public List<Item> getItemsByUserId(long userId) {
        log.debug("Getting all items by userId {}", userId);
        return userItemIndex.get(userId);
    }

    @Override
    public List<Item> getItemsBySearch(String text) {
        log.info("Getting items by search text: {}.", text);
        return items.values().stream()
                .filter(item -> item.getAvailable() &&
                        ((item.getName().toLowerCase().contains(text.toLowerCase())) ||
                                (item.getDescription().toLowerCase().contains(text.toLowerCase()))))
                .collect(Collectors.toList());
    }
}