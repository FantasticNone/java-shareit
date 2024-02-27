package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
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

    ItemResponseDto itemResponseDto;

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

        itemResponseDto = ItemResponseDto.builder()
                .id(1L)
                .description("I want the magic stick")
                .build();

        itemRequest = ItemRequest.builder()
                .created(LocalDateTime.now())
                .description(itemResponseDto.getDescription())
                .requester(requester)
                .build();

        itemRequestSaved = ItemRequest.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .description(itemResponseDto.getDescription())
                .requester(requester)
                .build();

    }

    @Test
    public void createRequestTest() {

        when(userRepository.findById(requester.getId())).thenReturn(Optional.of(requester));

        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequestSaved);

        ItemRequestDto expected = ItemRequestMapper.toItemRequestDto(itemRequestSaved);

        ItemRequestDto result = itemRequestService.createRequest(requester.getId(), itemResponseDto);

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

        List<ItemRequestDto> result = itemRequestService.getUserRequests(userId);

        assertNotNull(result);
    }

    @Test
    public void getRequestTest() {

        long userId = 1L;
        long requestId = 1L;

        when(userRepository.existsById(userId)).thenReturn(true);
        ItemRequest itemRequest = new ItemRequest();
        when(requestRepository.findById(requestId)).thenReturn(Optional.of(itemRequest));

        ItemRequestDto result = itemRequestService.getRequest(userId, requestId);

        assertNotNull(result);
    }

    @Test
    public void getAllRequestsTest() {

        long userId = 1L;
        int from = 0;
        int size = 10;
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        List<ItemRequest> itemRequests = new ArrayList<>();
        when(requestRepository.findAllByRequesterNotOrderByCreatedDesc(user, PageRequest.of(0, 10))).thenReturn(itemRequests);

        List<ItemRequestDto> result = itemRequestService.getAllRequests(userId, from, size);

        assertNotNull(result);
    }
}
