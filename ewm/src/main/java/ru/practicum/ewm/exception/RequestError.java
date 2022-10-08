package ru.practicum.ewm.exception;

public class RequestError extends RuntimeException {

    public RequestError(String message) {
        super(message);
    }
}
