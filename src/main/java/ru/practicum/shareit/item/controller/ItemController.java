package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.utils.Marker;
import ru.practicum.shareit.valid.PageableValidator;

import javax.validation.Valid;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {

    private final ItemService itemService;
    private final PageableValidator pageableValidator;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemDtoMarker createItem(@Validated(Marker.Create.class) @RequestBody ItemDtoMarker itemDto,
                                    @RequestHeader(USER_ID) long userId) {
        log.info("Creating item {}", itemDto);
        return itemService.create(itemDto, userId);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@PathVariable long itemId, @Validated(Marker.Update.class) @RequestBody ItemDtoMarker itemRequestDto,
                              @RequestHeader(USER_ID) long userId) {
        log.info("Updating item by id {}", itemId);
        return itemService.update(itemId, itemRequestDto, userId);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItemById(@PathVariable Long itemId,
                               @RequestHeader(USER_ID) long userId) {
        log.info("GET \"items/{}\" Header \"X-Sharer-User-Id\"={}", itemId, userId);
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping()
    public List<ItemDto> getUserItems(@RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size,
                                      @RequestHeader(USER_ID) long userId) {
        pageableValidator.checkingPageableParams(from, size);
        log.info("Getting all items by userId {}", userId);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsByUserId(userId, page);
    }

    @GetMapping("/search")
    public List<ItemDto> getItemsBySearch(@RequestParam(defaultValue = "0") Integer from,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          String text) {
        pageableValidator.checkingPageableParams(from, size);
        log.info("Getting items by search text: {}", text);
        Pageable page = PageRequest.of(from / size, size);
        return itemService.getItemsBySearch(text, page);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto postComment(@RequestHeader(USER_ID) long userId,
                                  @PathVariable long itemId,
                                  @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Posting comment from user id {} to item id {}", userId, itemId);
        return itemService.postComment(userId, itemId, commentRequestDto);
    }
}