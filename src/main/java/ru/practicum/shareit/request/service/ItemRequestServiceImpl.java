package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoMarker;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.request.dto.mapper.ItemRequestMapper.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    private final ItemRequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public ItemResponseDto createRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = getUserById(userId);
        ItemRequest newRequest = createNewRequest(user, itemRequestDto);
        return saveAndGetItemResponseDto(newRequest);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getUserRequests(long userId) {
        User user = getUserById(userId);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterOrderByCreatedDesc(user);
        Map<ItemRequest, List<Item>> itemsMap = getItemsMap(itemRequests);
        return mapToItemRequestDtos(itemRequests, itemsMap);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemResponseDto getRequest(Long userId, Long requestId) {
        validateUserExists(userId);
        ItemRequest itemRequest = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Item request not found"));
        List<ItemDtoMarker> itemDtoMarker = itemRepository.findAllByRequestInOrderById(List.of(itemRequest)).stream()
                .map(ItemMapper::toItemDtoMarker)
                .collect(Collectors.toList());
        ItemResponseDto itemResponseDto = ItemRequestMapper.toItemResponseDto(itemRequest);
        itemResponseDto.setItems(itemDtoMarker);
        return itemResponseDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemResponseDto> getAllRequests(Long userId, Integer from, Integer size) {
        User user = getUserById(userId);
        validatePaginationParams(from, size);
        Pageable page = createPageable(from, size);
        List<ItemRequest> itemRequests = requestRepository.findAllByRequesterNotOrderByCreatedDesc(user, page);
        Map<ItemRequest, List<Item>> itemsMap = getItemsMap(itemRequests);
        return mapToItemRequestDtos(itemRequests, itemsMap);
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    private ItemRequest createNewRequest(User user, ItemRequestDto itemRequestDto) {
        ItemRequest newRequest = ItemRequestMapper.toItemRequest(itemRequestDto);
        newRequest.setCreated(LocalDateTime.now());
        newRequest.setRequester(user);
        return requestRepository.save(newRequest);
    }

    private ItemResponseDto saveAndGetItemResponseDto(ItemRequest newRequest) {
        return toItemResponseDto(newRequest);
    }

    private Map<ItemRequest, List<Item>> getItemsMap(List<ItemRequest> itemRequests) {
        List<Item> items = itemRepository.findAllByRequestInOrderById(itemRequests);
        return items.stream().collect(Collectors.groupingBy(Item::getRequest));
    }

    private void validatePaginationParams(Integer from, Integer size) {
        if (from != null && size != null && (from < 0 || size < 0)) {
            throw new DataException("Invalid pagination data");
        }
    }

    private Pageable createPageable(Integer from, Integer size) {
        int pageNumber = from / size;
        return PageRequest.of(pageNumber, size);
    }

    private List<ItemResponseDto> mapToItemRequestDtos(List<ItemRequest> itemRequests, Map<ItemRequest, List<Item>> itemsMap) {
        return itemRequests.stream().map(request -> {
            List<ItemDtoMarker> itemDtoMarkers = itemsMap.getOrDefault(request, Collections.emptyList())
                    .stream()
                    .map(ItemMapper::toItemDtoMarker)
                    .collect(Collectors.toList());
            ItemResponseDto itemResponseDto = ItemRequestMapper.toItemResponseDto(request);
            itemResponseDto.setItems(itemDtoMarkers);
            return itemResponseDto;
        }).collect(Collectors.toList());
    }

    private void validateUserExists(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User not found");
        }
    }
}