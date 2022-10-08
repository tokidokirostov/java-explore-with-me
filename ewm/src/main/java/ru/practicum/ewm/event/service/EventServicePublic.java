package ru.practicum.ewm.event.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.client.EndpointHit;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.dto.EventDto;
import ru.practicum.ewm.user.event.dto.EventMapper;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.storage.EventStorage;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventServicePublic {
    @Autowired
    private final EventStorage eventStorage;
    @Autowired
    private final StatClient statClient;

    //Получение подробной информации об опубликованном событии по его идентификатору
    public EventDto getEvent(String idS) {
        try {
            Long id = Long.parseLong(idS);
            Event event = eventStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            if (event.getState().equals("PUBLISHED")) {
                return EventMapper.toEventDto(event);
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Получение событий с возможностью фильтраций
    public List<EventDto> getEventsPublic(String text, List<String> categoriesSt, String paidS, String rangeStartSt, String rangeEndSt,
                                          String onlyAvailableS, String sortS, String fromS, String sizeS) {
        try {
            Integer from = Integer.parseInt(fromS);
            Integer size = Integer.parseInt(sizeS);
            List<Long> categories = new ArrayList<>();
            for (String s : categoriesSt) {
                categories.add(Long.parseLong(s));
            }
            Boolean paid = Boolean.parseBoolean(paidS);
            Boolean onlyAvailable = Boolean.parseBoolean(onlyAvailableS);
            if (onlyAvailable) {
                onlyAvailable = null;
            }
            if (text != null) {
                text = text.toLowerCase();
            }
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(from, size, sort);
            LocalDateTime rangeStart = LocalDateTime.now();
            if (rangeStartSt != null) {
                rangeStart = LocalDateTime.parse(rangeStartSt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            LocalDateTime rangeEnd = LocalDateTime.parse("3000-01-06 13:30:38", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (rangeEndSt != null) {
                rangeEnd = LocalDateTime.parse(rangeEndSt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            Page<Event> eventList = eventStorage.searchEvents(text, categories, paid, onlyAvailable,
                    rangeStart, rangeEnd, page);
            if (sortS.equals("EVENT_DATE")) {
                return eventList
                        .stream()
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(event -> EventMapper.toEventDto(event))
                        .collect(Collectors.toList());
            } else {
                return eventList
                        .stream()
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(event -> EventMapper.toEventDto(event))
                        .collect(Collectors.toList());
            }
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Сохранение информации о том, что к эндпоинту был запрос
    public void createStat(HttpServletRequest httpServletRequest) {
        EndpointHit endpointHit = EndpointHit.builder()
                .id(null)
                .app("ewm")
                .uri(httpServletRequest.getRequestURI())
                .ip(httpServletRequest.getRemoteUser())
                .timestamp(LocalDateTime.now())
                .build();
        statClient.createHit(endpointHit);

    }
}
