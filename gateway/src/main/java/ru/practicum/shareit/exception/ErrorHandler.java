package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    @ExceptionHandler ({MethodArgumentNotValidException.class, BadRequestException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidateException(final Exception e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return new ErrorResponse(e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<String> handleValidateException(final ConstraintViolationException e) {
        log.debug("Получен статус 400 Bad Request {}", e.getMessage(), e);
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return new ErrorResponse("Internal Server Error");
    }
}
