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
        for (BookingState state : BookingState.values()) {
            if (value.equalsIgnoreCase(state.name())) {
                return state;
            }
        }
        throw new BadRequestException("Unknown state: " + value);
    }
}