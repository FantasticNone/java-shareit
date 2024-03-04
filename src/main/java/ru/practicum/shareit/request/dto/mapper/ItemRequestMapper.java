package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemRequestDto itemRequestDto) {
        return ItemRequest
                .builder()
                .description(itemRequestDto.getDescription())
                .build();
    }

    public ItemResponseDto toItemResponseDto(ItemRequest itemRequest) {
        return ItemResponseDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(List.of())
                .created(itemRequest.getCreated())
                .build();
    }

    public List<ItemResponseDto> requestListToDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::toItemResponseDto).collect(Collectors.toList());
    }
}
