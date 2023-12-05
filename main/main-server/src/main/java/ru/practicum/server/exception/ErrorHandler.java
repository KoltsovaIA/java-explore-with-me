package ru.practicum.server.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.main.api.error.ErrorResponse;
import ru.practicum.main.util.exception.AlreadyExistsException;
import ru.practicum.main.util.exception.NotFoundException;

import javax.validation.ValidationException;
import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({Throwable.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleInternalServerError(final RuntimeException e) {
        log.debug("Получен статус 500 Internal Server Error {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .reason(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({NotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFound(final RuntimeException e) {
        log.debug("Получен статус 404 Not Found Error {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.NOT_FOUND.value())
                .reason(HttpStatus.NOT_FOUND.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({AlreadyExistsException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExists(final RuntimeException e) {
        log.debug("Получен статус 409 Already Exists - Conflict Error {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.CONFLICT.value())
                .reason(HttpStatus.CONFLICT.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        log.debug("Получен статус 400 Bad Request Error {}", e.getMessage(), e);
        return ErrorResponse.builder()
                .status(HttpStatus.BAD_REQUEST.value())
                .reason(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(e.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
    }
}
