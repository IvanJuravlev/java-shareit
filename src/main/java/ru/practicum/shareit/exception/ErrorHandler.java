package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
@Slf4j
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerChangeException(final ChangeException exception) {
        log.warn("404 {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handlerDuplicateEmailException(final DuplicatedEmailException exception) {
        log.warn("409 {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handlerNotFoundException(final NotFoundException exception) {
        log.warn("404 {}", exception.getMessage());
        return exception.getMessage();
    }
}
