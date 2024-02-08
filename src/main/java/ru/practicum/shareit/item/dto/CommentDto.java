package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Builder
@Data
public class CommentDto {

    private Long id;

    @Size(max = 255, message = "The text must be no more than 255 characters")
    private String text;

    private String authorName;

    private LocalDateTime created;
}