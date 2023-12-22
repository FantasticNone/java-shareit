package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.utils.Marker;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDto createItem(@Validated(Marker.Create.class) @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) long userId) {
        log.debug("Creating item {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @RequestBody ItemDto itemDto,
                              @RequestHeader(USER_ID) long userId) {
        log.debug("Updating item by id {}", itemId);
        return itemService.update(itemId, itemDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId, @RequestHeader(USER_ID) long userId) {
        log.debug("Getting item by id : {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getUserItems(@RequestHeader(USER_ID) long userId) {
        log.debug("Getting all items by userId {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(String text) {
        log.debug("Getting items by search text: {}", text);
        return itemService.getItemsBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID) long userId,
                                  @PathVariable long itemId,
                                  @Valid @RequestBody CommentDto commentDto) {
        log.debug("Posting comment from user id {} to item id {}", userId, itemId);
        return itemService.postComment(userId, itemId, commentDto);
    }
}