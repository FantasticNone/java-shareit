package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.valid.PageableValidator;

import javax.validation.constraints.Min;
import java.util.List;


@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto createItemRequest(@RequestHeader(USER_ID) long userId,
                                            @RequestBody ItemResponseDto itemResponseDto) {
        log.info("Creating item request from user id {}", userId);
        return itemRequestService.createRequest(userId, itemResponseDto);
    }

    @GetMapping
    public List<ItemRequestDto> getUserRequests(@RequestHeader(USER_ID) long userId) {
        log.info("Getting all user requests user id {}", userId);
        return itemRequestService.getUserRequests(userId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(USER_ID) long userId,
                                               @RequestParam(defaultValue = "1") @Min(0) int from,
                                               @RequestParam(defaultValue = "10") @Min(1) int size) {
        log.info("Getting all requests user id {} from {} size {}", userId, from, size);
        return itemRequestService.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getItemRequest(@RequestHeader(USER_ID) long userId,
                                         @PathVariable long requestId) {
        log.info("Getting item request id {} from user id {}", requestId, userId);
        return itemRequestService.getRequest(userId, requestId);
    }


}
