package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.utils.HttpHeaders;
import ru.practicum.shareit.utils.Marker;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(Marker.Create.class)
                                             @RequestBody ItemDtoMarker itemDto,
                                             @RequestHeader(HttpHeaders.USER_ID) long userId) {
        log.info("Creating item {}", itemDto);
        return itemClient.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@PathVariable long itemId,
                                             @Validated(Marker.Update.class)
                                             @RequestBody ItemDtoMarker itemRequestDto,
                                             @RequestHeader(HttpHeaders.USER_ID) long userId) {
        log.info("Update item id {}", itemId);
        return itemClient.update(itemId, itemRequestDto, userId);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItem(@PathVariable long itemId,
                                          @RequestHeader(HttpHeaders.USER_ID) long userId) {
        log.info("Get item id {}", itemId);
        return itemClient.getItem(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getUserItems(@RequestHeader(HttpHeaders.USER_ID) long userId) {
        log.info("Get user {} items", userId);
        return itemClient.getUserItems(userId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                             @Positive @RequestParam(defaultValue = "10") Integer size,
                                             String text) {
        log.info("Getting items by search text: {}", text);
        return itemClient.search(text, from, size);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> postComment(@RequestHeader(name = "X-Sharer-User-Id") long userId,
                                              @PathVariable long itemId,
                                              @Valid @RequestBody CommentRequestDto commentRequestDto) {
        log.info("Post comment userid {} itemid {}", userId, itemId);
        return itemClient.postComment(userId, itemId, commentRequestDto);
    }
}