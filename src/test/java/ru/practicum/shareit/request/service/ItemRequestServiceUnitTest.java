package ru.practicum.shareit.request.service;

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
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

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

    @Test
    public void createRequestTest() {

        long userId = 1L;
        ItemResponseDto itemResponseDto = new ItemResponseDto(1L, "Sample Description");
        User user = new User();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        ItemRequest newRequest = new ItemRequest();
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(newRequest);

        ItemRequestDto result = itemRequestService.createRequest(userId, itemResponseDto);

        assertNotNull(result);
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
