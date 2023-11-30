package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.comment.dto.CommentDtoIn;
import ru.practicum.shareit.item.comment.dto.CommentDtoOut;
import ru.practicum.shareit.item.dto.ItemDtoIn;
import ru.practicum.shareit.item.dto.ItemDtoOut;
import ru.practicum.shareit.utils.Marker;

import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoOut createItem(@Validated(Marker.Create.class) @RequestBody ItemDtoIn itemDtoIn,
                                 @RequestHeader(USER_ID) long userId) {
        log.debug("Creating item {}", itemDtoIn.getName());
        return itemService.create(itemDtoIn, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDtoOut updateItem(@PathVariable long itemId,
                                 @Validated(Marker.Update.class) @RequestBody ItemDtoIn itemDtoIn,
                                 @RequestHeader(USER_ID) long userId) {
        log.debug("Updating item by id {}", itemId);
        return itemService.update(itemId, itemDtoIn, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDtoOut getItemById(@PathVariable Long itemId,
                                  @RequestHeader(USER_ID) long userId) {
        log.debug("Getting item by id : {}", itemId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDtoOut> getUserItems(@RequestHeader(USER_ID) long userId) {
        log.debug("Getting all items by userId {}", userId);
        return itemService.getItemsByUserId(userId);
    }

    @GetMapping("/search")
    public List<ItemDtoOut> getItemsBySearch(String text) {
        log.debug("Getting items by search text: {}", text);
        return itemService.getItemsBySearch(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoOut addComment(@PathVariable long itemId,
                                    @Validated(Marker.Create.class) @RequestBody CommentDtoIn commentDtoIn,
                                    @RequestHeader(USER_ID) long userId) {
        log.debug("Creating comment by item: {}", itemId);
        return itemService.saveNewComment(itemId, commentDtoIn, userId);
    }
}