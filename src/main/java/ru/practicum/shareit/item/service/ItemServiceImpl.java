package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserService userService;
    private final ItemRepository itemRepository;

    @Override
    public ItemDto create(ItemDto itemDto, long userId) {
        log.debug("Creating item : {}; for user {}", itemDto, userId);
        userService.getById(userId);
        return ItemMapper.toItemDto(itemRepository.create(ItemMapper.toItem(itemDto), userId));
    }

    @Override
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        userService.getById(userId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", Item.class)));
        String name = itemDto.getName();
        String description = itemDto.getDescription();
        Boolean available = itemDto.getAvailable();
        if (item.getOwner() == userId) {
            if (name != null && !name.isBlank()) {
                item.setName(name);
            }
            if (description != null && !description.isBlank()) {
                item.setDescription(description);
            }
            if (available != null) {
                item.setAvailable(available);
            }
        } else {
            throw new NotOwnerException(String.format("Owner id is incorrect!",
                    userId, name));
        }
        log.debug("Updating item : {}; for user {}", item, userId);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto getItemById(long itemId) {
        log.debug("Getting item by id : {} ", itemId);
        Item item = itemRepository.getItemById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", Item.class)));
        return ItemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(long userId) {
        log.debug("Getting items by user Id : {} ", userId);
        userService.getById(userId);
        return itemRepository.getItemsByUserId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemsBySearch(String text) {
        log.debug("Getting items by search : {} ", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getItemsBySearch(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}