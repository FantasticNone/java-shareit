package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemResponseDto createRequest(long userId, ItemRequestDto itemRequestDto);

    List<ItemResponseDto> getUserRequests(long userId);

    List<ItemResponseDto> getAllRequests(Long userId,  Integer from, Integer size);

    ItemResponseDto getRequest(Long userId, Long requestId);
}
