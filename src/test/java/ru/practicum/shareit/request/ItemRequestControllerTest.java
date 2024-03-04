package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.utils.HttpHeaders;

import java.time.LocalDateTime;
import java.util.Collections;

import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;

    @MockBean
    private ItemRequestService itemRequestService;

    @Test
    void createItemRequestTest() throws Exception {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test Description")
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.createRequest(anyLong(), any(ItemRequestDto.class))).willReturn(itemResponseDto);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_ID, 123)
                        .content(mapper.writeValueAsString(itemRequestDto)))
                .andExpect(status().isOk());
    }

    @Test
    void createItemRequest_WhenValidationFails_Expect400BadRequest() throws Exception {

        ItemRequestDto itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("Test Description")
                .build();

        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        itemRequestDto.setDescription(" ");
        long userId = 1L;
        mockMvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemResponseDto))
                        .header(HttpHeaders.USER_ID, userId)
                        .contentType("application/json"))
                .andExpect(status().isBadRequest());
        verify(itemRequestService, never()).createRequest(userId, itemRequestDto);
    }

    @Test
    void getUserRequestsTest() throws Exception {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.getUserRequests(anyLong())).willReturn(Collections.singletonList(itemResponseDto));

        mockMvc.perform(get("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_ID, 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)));
    }

    @Test
    void getAllRequestsTest() throws Exception {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.getAllRequests(anyLong(), anyInt(), anyInt())).willReturn(Collections.singletonList(itemResponseDto));

        mockMvc.perform(get("/requests/all")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_ID, 123)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", equalTo(1)));
    }

    @Test
    void getItemRequestTest() throws Exception {
        ItemResponseDto itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("Test Description")
                .created(LocalDateTime.now())
                .items(Collections.emptyList())
                .build();

        given(itemRequestService.getRequest(anyLong(), anyLong())).willReturn(itemResponseDto);

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(HttpHeaders.USER_ID, 123))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", equalTo(1)));
    }
}
