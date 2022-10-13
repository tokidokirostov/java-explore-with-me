package ru.practicum.statisrics.dto;

import ru.practicum.statisrics.model.EndpointHit;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class EndpointHitMapper {
    public static EndpointHitDto toEndpointHitDto(EndpointHit endpointHit) {
        return new EndpointHitDto(
                endpointHit.getId(),
                endpointHit.getIp(),
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getTimestamp().toString()
        );
    }

    public static EndpointHit toEndpointHit(EndpointHitDto endpointHit) {
        return new EndpointHit(
                null,
                endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                LocalDateTime.parse(endpointHit.getTimestamp(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
        );
    }

}
