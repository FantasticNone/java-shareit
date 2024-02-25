package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createRequest(long userId, ItemResponseDto itemResponseDto);

    List<ItemRequestDto> getUserRequests(long userId);

    List<ItemRequestDto> getAllRequests(Long userId,  Integer from, Integer size);

    ItemRequestDto getRequest(Long userId, Long requestId);
}
