package ru.practicum.shareit.booking;

import ru.practicum.shareit.exception.BadRequestException;

public enum BookingState {
    ALL,
    CURRENT,
    FUTURE,
    PAST,
    WAITING,
    REJECTED;

    public static BookingState getEnumByString(String value) {
        BookingState bookingState;
        try {
            bookingState = BookingState.valueOf(value);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Unknown state: " + value);
        }
        return bookingState;
    }
}