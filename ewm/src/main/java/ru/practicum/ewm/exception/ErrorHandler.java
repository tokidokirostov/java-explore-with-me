package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public CustomErrorVol2 handleUserNotFoundException(final UserNotFoundException e) {
        return new CustomErrorVol2("NOT_FOUND", "The required object was not found.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public CustomError handleUserNotFoundException(final RequestError e) {
        return new CustomError(new String[]{}, "Only pending or canceled events can be changed",
                "For the requested operation the conditions are not met.", e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public CustomError handleForbiddenException(final ForbiddenError e) {
        return new CustomError(new String[]{}, "Only pending or canceled events can be changed",
                "For the requested operation the conditions are not met.", e.getMessage());
    }
}
