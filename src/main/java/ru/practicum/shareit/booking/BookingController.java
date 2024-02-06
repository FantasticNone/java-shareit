package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingRequestDto;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;
    private static final String USER_ID = "X-Sharer-User-Id";

    @PostMapping
    public BookingDto create(@Valid @RequestBody BookingRequestDto bookingDto,
                             @RequestHeader(USER_ID) Long userId) {
        log.info("Creating booking by user id {}", userId);
        bookingDto.setId(userId);
        return bookingService.create(bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approve(@PathVariable Long bookingId,
                              @RequestHeader(USER_ID) Long userId,
                              @RequestParam Boolean approved) {
        if (approved) {
            log.info("Approved booking id {} by user id {}", bookingId, userId);
        }
        return bookingService.approve(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBooking(@PathVariable Long bookingId,
                                 @RequestHeader(USER_ID) Long userId) {
        log.info("Getting booking id {} by user id {}", bookingId, userId);
        return bookingService.getBooking(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestHeader(USER_ID) Long userId,
                                         @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting all user bookings", state, userId);
        return bookingService.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestHeader(USER_ID) Long userId,
                                          @RequestParam(defaultValue = "ALL") String state) {
        log.info("Getting all owner bookings", state, userId);
        return bookingService.getAllByOwner(userId, state);
    }
}
