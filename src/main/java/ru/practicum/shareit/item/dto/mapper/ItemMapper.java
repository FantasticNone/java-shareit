package ru.practicum.shareit.item.dto.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@UtilityClass
public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .userId(item.getOwner() != null ? item.getOwner().getId() : null)
                .comments(mapCommentEntitiesToDtos(item.getComments()))
                .build();
    }

    public static Item toItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    private static List<CommentDto> mapCommentEntitiesToDtos(List<Comment> comments) {
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