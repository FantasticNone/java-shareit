package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.mapper.BookingMapper;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.DataException;
import ru.practicum.shareit.exception.ItemBookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.BookingStatus.*;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final UserService userService;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private static final Sort SORT_BY_START_DESC = Sort.by(Sort.Direction.DESC, "start");

    @Override
    @Transactional
    public BookingDto create(BookingRequestDto bookingRequestDto) {
        User user = userRepository.findById(bookingRequestDto.getId())
                .orElseThrow(() -> new NotFoundException("User not found"));
        Item item = itemRepository.findById(bookingRequestDto.getItemId())
                .orElseThrow(() -> new NotFoundException("Item not found"));
        if (item.getOwner().equals(user))
            throw new NotFoundException("Item already booked");

        if (item.getAvailable()) {
            Booking booking = Booking.builder()
                    .start(bookingRequestDto.getStart())
                    .end(bookingRequestDto.getEnd())
                    .booker(user)
                    .item(item)
                    .status(BookingStatus.WAITING)
                    .build();

            return BookingMapper.responseDtoOf(bookingRepository.save(booking));
        } else {
            throw new ItemBookingException("Item is unavailable");
        }
    }

    @Override
    @Transactional
    public BookingDto approve(Long bookingId, Long userId, Boolean approved) {
        userService.getById(userId);
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));
        if (!userId.equals(booking.getItem().getOwner().getId())) {
            throw new NotFoundException("User has no rights to approve booking ");
        }
        if (!booking.getStatus().equals(WAITING) && booking.getItem().getAvailable()) {
            throw new BadRequestException("Only WAITING can be approved or rejected");
        }
        if (approved) {
            booking.setStatus(APPROVED);
        } else {
            booking.setStatus(REJECTED);
        }

        return BookingMapper.responseDtoOf(booking);
    }

    @Override
    public BookingDto getBooking(Long userId, Long bookingId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Booking not found"));

        if (!booking.getItem().getOwner().getId().equals(userId) && !booking.getBooker().getId().equals(userId)) {
            throw new NotFoundException("No booking found for user ");
        }

        return BookingMapper.responseDtoOf(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByUser(Long userId, String state) {
        if (!userRepository.existsById(userId))
            throw new NotFoundException("User not found");

        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = bookingRepository.findAllByBooker_Id(userId, SORT_BY_START_DESC);
                break;
            case "CURRENT":
                bookings = bookingRepository.findAllByBookerIdAndCurrentStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case "FUTURE":
                bookings = bookingRepository.findAllByBookerIdAndFutureStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case "PAST":
                bookings = bookingRepository.findAllByBookerIdAndPastStatus(userId, LocalDateTime.now(), SORT_BY_START_DESC);
                break;
            case "WAITING":
                bookings = bookingRepository.findAllByBookerIdAndWaitingStatus(userId, BookingStatus.WAITING, SORT_BY_START_DESC);
                break;
            case "REJECTED":
                bookings = bookingRepository.findAllByBookerIdAndRejectedStatus(userId, Collections.singletonList(BookingStatus.REJECTED), SORT_BY_START_DESC);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return BookingMapper.responseDtoListOf(bookings);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getAllByOwner(Long ownerId, String state) {
        User owner = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException("User id %d not found"));

        Optional<List<Item>> userItemsOptional = Optional.ofNullable(itemRepository.findByOwner(owner, Sort.by(Sort.Direction.ASC, "id")));
        List<Item> userItems = userItemsOptional.orElseThrow(() -> new DataException("User has no items"));
        if (userItems.size() < 1) {
            throw new DataException("User has less than 1, minimum 1 item required");
        }
        List<Long> userItemsIds = userItems.stream().map(Item::getId).collect(Collectors.toList());

        List<Booking> bookings;

        switch (state) {
            case "ALL":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItems(userItemsIds, SORT_BY_START_DESC));
                break;
            case "CURRENT":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndCurrentStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            case "FUTURE":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndFutureStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            case "PAST":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndPastStatus(userItemsIds, LocalDateTime.now(), SORT_BY_START_DESC));
                break;
            case "WAITING":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndWaitingStatus(userItemsIds, BookingStatus.WAITING, SORT_BY_START_DESC));
                break;
            case "REJECTED":
                bookings = new ArrayList<>(bookingRepository.findAllByOwnerItemsAndRejectedStatus(userItemsIds, List.of(BookingStatus.REJECTED, BookingStatus.CANCELED), SORT_BY_START_DESC));
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return BookingMapper.responseDtoListOf(bookings);
    }
}