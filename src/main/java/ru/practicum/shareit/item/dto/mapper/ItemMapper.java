package ru.practicum.shareit.item.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class ItemMapper {

    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .userId(item.getOwner() != null ? item.getOwner().getId() : null)
                .comments(mapCommentEntitiesToDtos(item.getComments()))
                .build();
    }

    public Item toItem(ItemDtoMarker itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public ItemDtoMarker toItemDtoMarker(Item item) {
        ItemDtoMarker itemDtoMarker = ItemDtoMarker.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .build();
        ItemRequest itemRequest = item.getRequest();
        if (itemRequest != null) {
            Long requestId = itemRequest.getId();
            itemDtoMarker.setRequestId(requestId);
        }
        return itemDtoMarker;
    }

    private List<CommentDto> mapCommentEntitiesToDtos(List<Comment> comments) {
        if (comments != null) {
            return comments.stream()
                    .map(comment -> CommentDto.builder()
                            .id(comment.getId())
                            .text(comment.getText())
                            .authorName(comment.getAuthor() != null ? comment.getAuthor().getName() : null)
                            .created(comment.getCreated())
                            .build())
                    .collect(Collectors.toList());
        }
        return new ArrayList<>();
    }
}