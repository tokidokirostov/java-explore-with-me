package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.service.EventServicePublic;
import ru.practicum.ewm.user.event.dto.EventDto;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@AllArgsConstructor
public class EventControllerPublic {
    @Autowired
    private final EventServicePublic eventServicePublic;

    //Получение подробной информации об опубликованном событии по его идентификатору
    @GetMapping("{id}")
    public EventDto getEvent(@PathVariable(name = "id") String id,
                             HttpServletRequest httpServletRequest) {
        eventServicePublic.createStat(httpServletRequest);
        log.info("Получен запрос GET /events/{}", id);
        return eventServicePublic.getEvent(id);
    }

    //Получение событий с возможностью фильтраций
    //events?text=ddd&categories=1&paid=true&rangeStart=111&rangeEnd=11&onlyAvailable=false&sort=EVENT_DATE&from=0&size=10
    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) String text,
                                    @RequestParam(required = false) List<String> categories,
                                    @RequestParam(required = false) String paid,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(required = false) String onlyAvailable,
                                    @RequestParam(required = false) String sort,
                                    @RequestParam(defaultValue = "0") String from,
                                    @RequestParam(defaultValue = "10") String size,
                                    HttpServletRequest httpServletRequest) {
        log.info("Получен запрос GET /events/");
        eventServicePublic.createStat(httpServletRequest);
        return eventServicePublic.getEventsPublic(text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);
    }

}
