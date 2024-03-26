package ru.practicum.shareit.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
@AllArgsConstructor
public class ItemRequestDto {

    private Long id;
    @NotBlank
    @Size(max = 255, message = "The description must be no more than 255 characters")
    private String description;
}
