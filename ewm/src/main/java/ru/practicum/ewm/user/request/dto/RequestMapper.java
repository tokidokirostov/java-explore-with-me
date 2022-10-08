package ru.practicum.ewm.user.request.dto;

import ru.practicum.ewm.user.request.model.Request;

public class RequestMapper {
    public static RequestDto toRequestDto(Request request) {
        return new RequestDto(request.getCreated(),
                request.getEvent().getId(),
                request.getId(),
                request.getRequester().getId(),
                request.getStatus().toString());
    }
}
