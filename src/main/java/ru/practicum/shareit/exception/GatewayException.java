package ru.practicum.shareit.exception;

public class GatewayException extends RuntimeException {
    public GatewayException(String message) {
        super(message);
    }
}