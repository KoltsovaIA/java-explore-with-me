package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;


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

    @ExceptionHandler({ValidationException.class, MethodArgumentNotValidException.class, ResponseStatusException.class})
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
