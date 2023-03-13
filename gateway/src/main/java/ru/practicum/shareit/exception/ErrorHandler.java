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

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handlerBedRequestException(final BadRequestException exception) {
        log.warn("404 {}", exception.getMessage());
        return exception.getMessage();
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleNotSupportedStateException(final NotSupportedStateException exception) {
        log.error("Incorrect state {}", exception.getMessage());

        return new ErrorResponse("Unknown state: UNSUPPORTED_STATUS", exception.getMessage());
    }
}
