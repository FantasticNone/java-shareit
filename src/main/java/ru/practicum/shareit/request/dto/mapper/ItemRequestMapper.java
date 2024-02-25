package ru.practicum.shareit.request.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemRequestMapper {
    public ItemRequest toItemRequest(ItemResponseDto itemResponseDto) {
        return ItemRequest
                .builder()
                .description(itemResponseDto.getDescription())
                .build();
    }

    public ItemRequestDto toItemRequestDto(ItemRequest itemRequest) {
        return ItemRequestDto
                .builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .items(List.of())
                .created(itemRequest.getCreated())
                .build();
    }

    public static List<ItemRequestDto> requestListToDto(List<ItemRequest> itemRequests) {
        return itemRequests.stream().map(ItemRequestMapper::toItemRequestDto).collect(Collectors.toList());
    }
}
