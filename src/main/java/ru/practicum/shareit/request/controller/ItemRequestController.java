package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.utils.HttpHeaders;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemResponseDto createItemRequest(@RequestHeader(HttpHeaders.USER_ID) long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        log.info("Creating item request from user id {}", userId);
        return itemRequestService.createRequest(userId, itemRequestDto);
    }

    @GetMapping
    public List<ItemResponseDto> getUserRequests(@RequestHeader(HttpHeaders.USER_ID) long userId) {
        log.info("Getting all user requests user id {}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemResponseDto> getAllRequests(@RequestHeader(HttpHeaders.USER_ID) long userId,
                                               @PositiveOrZero @RequestParam(defaultValue = "0") Integer from,
                                               @Positive @RequestParam(defaultValue = "10") Integer size) {
        log.info("Getting all requests user id {} from {} size {}", userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemResponseDto getItemRequest(@RequestHeader(HttpHeaders.USER_ID) long userId,
                                         @PathVariable long requestId) {
        log.info("Getting item request id {} from user id {}", requestId, userId);
        return itemRequestService.getRequest(userId, requestId);
    }
}
