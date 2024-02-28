package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ErrorHandlerTest {

    private final ErrorHandler errorHandler = new ErrorHandler();

    @Test
    public void testHandleUserNotFoundException() {
        NotFoundException exception = new NotFoundException("User not found");
        ErrorResponse result = errorHandler.handleNotFoundException(exception);
        assertEquals("User not found", result.getError());
    }

    @Test
    public void testHandleEmailAlreadyExistsException() {
        EmailIsAlreadyRegisteredException exception = new EmailIsAlreadyRegisteredException("Email already exists");
        ErrorResponse result = errorHandler.handleEmailAlreadyExistsException(exception);
        assertEquals("Email already exists", result.getError());
    }

    @Test
    public void testHandleBadRequestException() {
        BadRequestException exception = new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        ErrorResponse result = errorHandler.handleValidateException(exception);
        assertEquals("Unknown state: UNSUPPORTED_STATUS", result.getError());
    }

    @Test
    public void testHandleGatewayHeaderException() {
        Throwable exception = new Throwable("Internal Server Error");
        ErrorResponse result = errorHandler.handleThrowable(exception);
        assertEquals("Internal Server Error", result.getError());
    }

    @Test
    public void testHandleNotOwnerException() {
        NotOwnerException exception = new NotOwnerException("User is not the owner of the item");
        ErrorResponse result = errorHandler.handleNotOwnerException(exception);
        assertEquals("User is not the owner of the item", result.getError());
    }
}
