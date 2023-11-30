package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.data.domain.Sort.Direction.DESC;

@Slf4j
@Transactional
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    public BookingDtoOut saveNewBooking(BookingDtoIn bookingDtoIn, long userId) {
        User booker = getUser(userId);
        Item item = getItem(bookingDtoIn.getItemId());
        if (!item.getAvailable()) {
            throw new ItemIsNotAvailableException("Item is not available for booking");
        }
        if (booker.getId() == item.getOwner().getId()) {
            throw new NotAvailableToBookOwnItemsException("There is no function to book your own item");
        }
        if (!bookingDtoIn.getEnd().isAfter(bookingDtoIn.getStart()) ||
                bookingDtoIn.getStart().isBefore(LocalDateTime.now())) {
            throw new WrongDatesException("The booking start date must be before the return date");
        }
        Booking booking = new Booking();
        booking.setItem(item);
        booking.setBooker(booker);
        bookingRepository.save(BookingMapper.toBooking(bookingDtoIn, booking));
        log.info("Booking with id {} has been created", booking.getId());
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Override
    public BookingDtoOut approve(long bookingId, Boolean isApproved, long userId) {
        User owner = getUser(userId);
        Booking booking = getById(bookingId);
        Item item = getItem(booking.getItem().getId());
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new ItemIsNotAvailableException("Item has already been booked");
        }
        if (owner.getId() != item.getOwner().getId()) {
            throw new IllegalVewAndUpdateException("Only the owner of the item can confirm the booking");
        }
        BookingStatus newBookingStatus = isApproved ? BookingStatus.APPROVED : BookingStatus.REJECTED;
        booking.setStatus(newBookingStatus);
        log.info("Booking with id {} has been updated", booking.getId());
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public BookingDtoOut getBookingById(long bookingId, long userId) {
        log.info("Getting booking by id: {}", bookingId);
        Booking booking = getById(bookingId);
        User booker = booking.getBooker();
        User owner = getUser(booking.getItem().getOwner().getId());
        if (booker.getId() != userId && owner.getId() != userId) {
            throw new IllegalVewAndUpdateException("Only the author or owner can view this booking");
        }
        return BookingMapper.toBookingDtoOut(booking);
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> getAllByBooker(String state, long bookerId) {
        User booker = getUser(bookerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByBookerId(booker.getId(), Sort.by(DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByBookerIdAndStateCurrent(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByBookerIdAndStatePast(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByBookerIdAndStateFuture(booker.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByBookerIdAndStatus(booker.getId(),
                        BookingStatus.REJECTED, Sort.by(DESC, "end"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public List<BookingDtoOut> getAllByOwner(long ownerId, String state) {
        User owner = getUser(ownerId);
        List<Booking> bookings;
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        switch (bookingState) {
            case ALL:
                bookings = bookingRepository.findAllByOwnerId(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findAllByOwnerIdAndStateCurrent(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findAllByOwnerIdAndStatePast(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findAllByOwnerIdAndStateFuture(owner.getId(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.WAITING, Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findAllByOwnerIdAndStatus(owner.getId(),
                        BookingStatus.REJECTED, Sort.by(Sort.Direction.DESC, "start"));
                break;
            default:
                throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings.stream().map(BookingMapper::toBookingDtoOut).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Booking getById(long bookingId) {
        log.info("Getting booking by id: {}", bookingId);
        return bookingRepository.findById(bookingId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", Booking.class)));
    }

    private User getUser(long userId) {
        return userRepository.findById(userId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", User.class)));
    }

    private Item getItem(long itemId) {
        return itemRepository.findById(itemId).orElseThrow(() ->
                new NotFoundException(String.format("Object of class %s not found", Item.class)));
    }
}