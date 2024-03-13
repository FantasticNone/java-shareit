package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentDtoTest {

    LocalDateTime localDateTime = LocalDateTime.now();

    User user = User.builder()
            .id(1L)
            .name("user")
            .email("email@mail.ru")
            .build();

    Item item = Item.builder()
            .id(1L)
            .owner(user)
            .name("drill")
            .description("drilling drill")
            .available(true)
            .build();

    Comment comment = Comment.builder()
            .id(1L)
            .author(user)
            .text("demo text")
            .created(localDateTime)
            .item(item)
            .build();

    @Test
    void responseDtoOf() {
        CommentDto expectedDto;
        CommentDto actualDto;

        expectedDto = CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(localDateTime)
                .text(comment.getText())
                .build();

        actualDto = CommentMapper.responseDtoOf(comment);

        assertEquals(expectedDto, actualDto);
    }

    @Test
    void listOf() {
        List<CommentDto> expectedList;
        List<CommentDto> actualList;

        CommentDto correctDto = CommentDto.builder()
                .id(comment.getId())
                .authorName(comment.getAuthor().getName())
                .created(localDateTime)
                .text(comment.getText())
                .build();

        expectedList = List.of(correctDto, correctDto, correctDto);
        actualList = CommentMapper.listOfComments(List.of(comment, comment, comment));

        assertEquals(expectedList, actualList);
    }
}
