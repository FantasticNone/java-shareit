package ru.practicum.shareit.item.comment.dto;

import lombok.Data;
import ru.practicum.shareit.utils.Marker;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
public class CommentDtoIn {
    @Size(max = 1000, groups = {Marker.Create.class, Marker.Update.class})
    @NotBlank(groups = {Marker.Create.class})
    private String text;
}