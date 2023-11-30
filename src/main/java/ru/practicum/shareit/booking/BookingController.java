package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoIn;
import ru.practicum.shareit.booking.dto.BookingDtoOut;
import ru.practicum.shareit.utils.Marker;


import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDtoOut saveNewBooking(@Validated(Marker.Create.class) @RequestBody BookingDtoIn bookingDtoIn,
                                        @RequestHeader(USER_ID) long userId) {
        log.debug("Save new booking {}", bookingDtoIn);
        return bookingService.saveNewBooking(bookingDtoIn, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoOut approve(@PathVariable long bookingId, @RequestParam(name = "approved") Boolean isApproved,
                                 @RequestHeader(USER_ID) long userId) {
        log.debug("Updating booking by id {}", bookingId);
        return bookingService.approve(bookingId, isApproved, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoOut getBookingById(@PathVariable long bookingId, @RequestHeader(USER_ID) long userId) {
        log.debug("Getting booking by id : {}", bookingId);
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoOut> getAllByBooker(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                              @RequestHeader(USER_ID) long bookerId) {
        log.debug("Getting all by booker {}", bookerId);
        return bookingService.getAllByBooker(state, bookerId);
    }

    @GetMapping("/owner")
    public List<BookingDtoOut> getAllByOwner(@RequestParam(name = "state", defaultValue = "ALL") String state,
                                             @RequestHeader(USER_ID) long ownerId) {
        log.debug("Getting all by owner {}", ownerId);
        return bookingService.getAllByOwner(ownerId, state);
    }
}