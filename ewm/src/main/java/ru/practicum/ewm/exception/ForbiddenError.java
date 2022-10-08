package ru.practicum.ewm.exception;

public class ForbiddenError extends RuntimeException {
    public ForbiddenError(String message) {
        super(message);
    }
}
