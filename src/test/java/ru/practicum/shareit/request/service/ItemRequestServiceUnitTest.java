package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemRequestServiceUnitTest {

    @Mock
    private ItemRequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    ItemRequestDto itemRequestDto;

    User requester;

    ItemRequest itemRequest;
    ItemRequest itemRequestSaved;

    @BeforeEach
    void setUp() {

        requester = User.builder()
                .id(1L)
                .name("requester")
                .email("requester@gmail.com")
                .build();

        itemRequestDto = ItemRequestDto.builder()
                .id(1L)
                .description("I need a motivation")
                .build();

        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .build();

        itemRequestSaved = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description(itemRequestDto.getDescription())
                .requester(requester)
                .build();

    }

    @Test
    public void createRequestTest() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequestSaved);

        ItemResponseDto expected = ItemRequestMapper.toItemResponseDto(itemRequestSaved);

        ItemResponseDto result = itemRequestService.createRequest(requester.getId(), itemRequestDto);

        assertNotNull(result);
        Assertions.assertEquals(expected, result);
    }

    @Test
    public void getUserRequestsTest() {

        long userId = 1L;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<ItemRequest> itemRequests = new ArrayList<>();
        when(requestRepository.findAllByRequesterOrderByCreatedDesc(user)).thenReturn(itemRequests);

        List<ItemResponseDto> result = itemRequestService.getUserRequests(userId);

        assertNotNull(result);
        Assertions.assertEquals(itemRequests, result);
    }


    @Test
    public void getRequestTest() {

        long userId = 1L;
        long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        ItemRequest itemRequest = new ItemRequest();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemResponseDto result = itemRequestService.getRequest(userId, requestId);

        ItemResponseDto expectedRequest = ItemRequestMapper.toItemResponseDto(itemRequest);

        assertNotNull(result);
        Assertions.assertEquals(expectedRequest, result);
    }

    @Test
    public void getAllRequestsTest() {

        long userId = 1L;
        int from = 0;
        int size = 10;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<ItemRequest> itemRequestsList = new ArrayList<>();
        when(requestRepository.findAllByRequesterNotOrderByCreatedDesc(user, PageRequest.of(0, 10))).thenReturn(itemRequestsList);

        List<ItemResponseDto> result = itemRequestService.getAllRequests(userId, from, size);
        List<ItemResponseDto> expectedList = ItemRequestMapper.requestListToDto(itemRequestsList);

        assertNotNull(result);
        Assertions.assertEquals(expectedList, result);
    }

    @Test
    public void getAllRequestsTestWithInvalidPaginationParams() {
        long userId = 1L;
        int from = -1;
        int size = 10;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        DataException exception = assertThrows(DataException.class, () -> {
            itemRequestService.getAllRequests(userId, from, size);
        });

        Assertions.assertEquals("Invalid pagination data", exception.getMessage());
    }
}
