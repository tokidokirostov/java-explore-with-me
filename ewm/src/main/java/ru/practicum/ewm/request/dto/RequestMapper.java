package ru.practicum.ewm.request.dto;

import ru.practicum.ewm.request.model.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }
}
