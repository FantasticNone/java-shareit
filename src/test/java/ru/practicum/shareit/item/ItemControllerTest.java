package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import org.springframework.http.MediaType;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.valid.PageableValidator;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ItemService itemService;
    @MockBean
    private PageableValidator pageableValidator;

    ItemDto itemRequestDto;
    ItemDto itemResponseDto;

    @BeforeEach
    void setUp() {

        long userId = 123L;

        itemRequestDto = ItemDto.builder()
                .name("drill")
                .description("drill for drilling")
                .available(true)
                .build();

        itemResponseDto = ItemDto.builder()
                .id(1L)
                .name(itemRequestDto.getName())
                .description(itemRequestDto.getDescription())
                .available(itemRequestDto.getAvailable())
                .userId(userId)
                .build();
    }

    @Test
    void createItem() throws Exception {

        ItemDtoMarker itemRequestDto = ItemDtoMarker.builder()
                .id(1L)
                .name("Updated Item Name")
                .description("Updated Description")
                .available(true)
                .build();

        when(itemService.create(itemRequestDto, 1L)).thenReturn(itemRequestDto);
        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .header("X-Sharer-User-Id", 1L)
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();
        verify(itemService).create(itemRequestDto, 1L);

    }

    @Test
    public void testUpdateItem() throws Exception {

        ItemDtoMarker updatedItemDto = ItemDtoMarker.builder()
                .id(1L)
                .name("Updated Item Name")
                .description("Updated Description")
                .available(true)
                .build();
        long userId = 123L;

        ItemDto existingItemDto = ItemDto.builder()
                .id(1L)
                .name("shovel")
                .description("shovel for digging")
                .userId(userId)
                .build();

        when(itemService.update(updatedItemDto.getId(), updatedItemDto, userId))
                .thenReturn(existingItemDto);

        mvc.perform(patch("/items/{itemId}", existingItemDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(updatedItemDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedItemDto.getId()))
                .andExpect(jsonPath("$.name").value(existingItemDto.getName()))
                .andExpect(jsonPath("$.description").value(existingItemDto.getDescription()));
    }


    @Test
    void getItemById() throws Exception {

        long userId = 123L;

        Mockito
                .when(itemService.getItemById(itemResponseDto.getId(), itemResponseDto.getUserId()))
                .thenReturn(itemResponseDto);

        mvc.perform(get("/items/{itemId}", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$.name").value(itemResponseDto.getName()));
    }

    @Test
    void getUserItems() throws Exception {
        List<ItemDto> expectedList;
        long userId = 123L;
        int from = 0;
        int size = 10;

        ItemDto newItemResponseDto = ItemDto.builder()
                .id(2L)
                .name("shovel")
                .description("shovel for digging")
                .available(true)
                .userId(userId)
                .build();

        expectedList = List.of(itemResponseDto, newItemResponseDto);

        Mockito
                .when(itemService.getItemsByUserId(userId, PageRequest.of(0, 10)))
                .thenReturn(expectedList);

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", String.valueOf(userId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[1].id").value(newItemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()))
                .andExpect(jsonPath("$[1].name").value(newItemResponseDto.getName()));
    }

    @Test
    void searchItem() throws Exception {
        long userId = 123L;
        int from = 0;
        int size = 10;

        Mockito
                .when(itemService.getItemsBySearch("drill", PageRequest.of(0, 10)))
                .thenReturn(List.of(itemResponseDto));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", String.valueOf(userId))
                        .param("text", "drill"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(itemResponseDto.getId()))
                .andExpect(jsonPath("$[0].name").value(itemResponseDto.getName()));
    }

    @Test
    void postComment() throws Exception {
        User owner = User.builder()
                .id(1L)
                .name("user")
                .email("email@gmail.com")
                .build();

        CommentRequestDto commentRequestDto = CommentRequestDto.builder()
                .text("text comment")
                .build();
        CommentDto commentResponseDto = CommentDto.builder()
                .id(1L)
                .authorName(owner.getName())
                .text(commentRequestDto.getText())
                .build();

        Mockito
                .when(itemService.postComment(owner.getId(), itemResponseDto.getId(), commentRequestDto))
                .thenReturn(commentResponseDto);

        mvc.perform(post("/items/{itemId}/comment", itemResponseDto.getId())
                        .header("X-Sharer-User-Id", String.valueOf(owner.getId()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(commentRequestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(commentResponseDto.getId()))
                .andExpect(jsonPath("$.text").value(commentResponseDto.getText()))
                .andExpect(jsonPath("$.authorName").value(owner.getName()));
    }
}