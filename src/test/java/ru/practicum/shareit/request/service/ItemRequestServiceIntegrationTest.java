package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import javax.transaction.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Transactional
@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class ItemRequestServiceIntegrationTest {

    @Autowired
    private ItemRequestService itemRequestService;

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;

    @Test
    public void createRequestTest() {
        long userId = 1L;
        ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Sample Description");
        User user = new User(1L, "user", "email@gmail.com");
        userRepository.save(user);

        ItemResponseDto result = itemRequestService.createRequest(userId, itemRequestDto);

        assertEquals("Sample Description", result.getDescription());

    }

    @Test
    public void getUserRequestsTest() {

        long userId = 1L;
        User user = new User(1L, "user", "email@gmail.com");
        userRepository.save(user);

        List<ItemResponseDto> result = itemRequestService.getUserRequests(userId);

        assertEquals(0, result.size());
    }

    @Test
    public void getRequestTest() {

        long userId = 1L;
        long requestId = 1L;

        User user = new User(1L, "user", "email@gmail.com");
        userRepository.save(user);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setDescription("Test request");
        itemRequest.setRequester(user);
        itemRequest.setCreated(LocalDateTime.now());
        ItemRequest savedRequest = requestRepository.save(itemRequest);

        ItemResponseDto result = itemRequestService.getRequest(userId, savedRequest.getId());

        assertEquals(requestId, result.getId());
    }

    @Test
    public void getAllRequestsTest() {

        long userId = 1L;
        int from = 0;
        int size = 10;
        User user = new User(1L, "user", "email@gmail.com");
        userRepository.save(user);

        List<ItemResponseDto> result = itemRequestService.getAllRequests(userId, from, size);

        assertEquals(0, result.size());
    }

}
