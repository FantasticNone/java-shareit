package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.mapper.ItemMapper;
import ru.practicum.shareit.item.dto.mapper.CommentMapper;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, long userId) {
        log.debug("Creating item: {}; for user {}", itemDto, userId);

        User user = userRepository.findById(itemDto.getUserId())
                .orElseThrow(() -> new NotFoundException(String.format("User id %d not found", itemDto.getUserId())));
        Item item = ItemMapper.toItem(itemDto);
        item.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto update(long itemId, ItemDto itemDto, long userId) {
        log.debug("Updating item with id: {} for user {}", itemId, userId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found"));
        if (item.getOwner().getId() == userId) {
            if (itemDto.getName() != null && !itemDto.getName().isBlank()) {
                item.setName(itemDto.getName());
            }
            if (itemDto.getDescription() != null && !itemDto.getDescription().isBlank()) {
                item.setDescription(itemDto.getDescription());
            }
            if (itemDto.getAvailable() != null) {
                item.setAvailable(itemDto.getAvailable());
            }
            Item updatedItem = itemRepository.save(item);
            return ItemMapper.toItemDto(updatedItem);
        } else {
            throw new NotOwnerException("User is not the owner of the item");
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(long itemId,long userId) {
        log.debug("Getting item by id: {}", itemId);
        Item item = itemRepository.findById(itemId).orElseThrow(() -> new NotFoundException("Item not found")); // Получение Item по его ID
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundException("User not found")); // Получение User по его ID
        ItemDto itemDto = ItemMapper.toItemDto(item);
        List<Comment> comments = commentRepository.findAllByItemId(itemId);

        if (item.getOwner().getId() == userId) {
            setLastAndNextBookingsForItem(itemDto);
        }

        itemDto.setComments(CommentMapper.listOfComments(comments));

        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUserId(Long userId) {
        log.debug("Getting items by user Id: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("User with not found")));

        List<Item> items = itemRepository.findByOwner(user, Sort.by("id").ascending());

        if (items.isEmpty()) {
            throw new NotFoundException(String.format("No items found for user"));
        }

        return setLastAndNextBookingsForItemList(items);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsBySearch(String text) {
        log.debug("Getting items by search: {}", text);
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        List<Item> items = itemRepository.findItemsByAvailableTrueAndNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(text, text); // Поиск Item по тексту
        return items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto postComment(Long userId, Long itemId, CommentDto commentRequestDto) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Item id %d not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User id %d not found"));

        if (itemRepository.hasValidBookingForItemAndUser(itemId, userId, LocalDateTime.now())) {
            Comment comment = Comment.builder()
                    .item(item)
                    .user(user)
                    .text(commentRequestDto.getText())
                    .created(LocalDateTime.now())
                    .build();

            comment = commentRepository.save(comment);
            return CommentMapper.responseDtoOf(comment);
        }

        throw new BadRequestException("User can not post comments on item");
    }

    private void setLastAndNextBookingsForItem(ItemDto itemDto) {
        Booking lastBooking = findLastBookingForItem(itemDto.getId());
        Booking nextBooking = findNextBookingForItem(itemDto.getId());

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.shortResponseDtoOf(lastBooking));
        }

        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.shortResponseDtoOf(nextBooking));
        }
    }

    private Booking findLastBookingForItem(Long itemId) {
        List<Booking> lastBookings = bookingRepository.findLastBookingByBookerId(itemId, Sort.by(Sort.Direction.DESC, "end_date"));
        return lastBookings.isEmpty() ? null : lastBookings.get(0);
    }

    private Booking findNextBookingForItem(Long itemId) {
        List<Booking> nextBookings = bookingRepository.findNextBookingByBookerId(itemId, Sort.by(Sort.Direction.ASC, "start_date"));
        return nextBookings.isEmpty() ? null : nextBookings.get(0);
    }

    private List<ItemDto> setLastAndNextBookingsForItemList(List<Item> items) {
        List<Long> itemIds = items.stream().map(Item::getId).collect(Collectors.toList());
        List<Booking> bookings = bookingRepository.findAllByItem_IdIn(itemIds, Sort.by(Sort.Direction.ASC, "start_date"));

        Map<Long, List<Booking>> mapBookings = new HashMap<>();

        for (Long itemId : itemIds) {
            mapBookings.put(itemId, new ArrayList<>());
        }

        for (Booking booking : bookings) {
            mapBookings.get(booking.getItem().getId()).add(booking);
        }

        return items.stream()
                .map(item -> setLastAndNextBookingsForItem(ItemMapper.toItemDto(item), (Map<Long, List<Booking>>) mapBookings.get(item.getId())))
                .collect(Collectors.toList());
    }


    private ItemDto setLastAndNextBookingsForItem(ItemDto itemDto, Map<Long, List<Booking>> mapBookings) {
        LocalDateTime now = LocalDateTime.now();

        List<Booking> itemBookings = mapBookings.getOrDefault(itemDto.getId(), Collections.emptyList());

        Optional<Booking> lastBookingOptional = itemBookings.stream()
                .filter(booking -> !booking.getStart().isAfter(now))
                .max(Comparator.comparing(Booking::getStart));

        Optional<Booking> nextBookingOptional = itemBookings.stream()
                .filter(booking -> !booking.getStart().isBefore(now))
                .min(Comparator.comparing(Booking::getStart));

        lastBookingOptional.ifPresent(booking -> itemDto.setLastBooking(BookingMapper.shortResponseDtoOf(booking)));
        nextBookingOptional.ifPresent(booking -> itemDto.setNextBooking(BookingMapper.shortResponseDtoOf(booking)));

        return itemDto;
    }
}
