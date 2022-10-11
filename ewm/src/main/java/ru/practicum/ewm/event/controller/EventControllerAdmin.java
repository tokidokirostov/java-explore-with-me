package ru.practicum.ewm.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.service.EventService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class EventControllerAdmin {
    @Autowired
    EventService eventService;

    //Публикация события
    @PatchMapping("{eventId}/publish")
    public EventDto eventPublish(@PathVariable String eventId) {
        log.info("---> Получен запрос PATCH admin/events/{}/publish", eventId);
        return eventService.eventPublish(eventId);
    }

    //Отклонение события
    @PatchMapping("{eventId}/reject")
    public EventDto eventReject(@PathVariable String eventId) {
        log.info("---> Получен запрос PATCH admin/events/{}/publish", eventId);
        return eventService.eventReject(eventId);
    }

    //Редактирование события
    @PutMapping("{eventId}")
    public EventDto eventPut(@RequestBody NewEventDto eventDto,
                             @PathVariable String eventId) {
        log.info("---> Получен запрос PUT admin/events/{} - NewEvent {}", eventId, eventDto);
        return eventService.eventPut(eventDto, eventId);
    }

    //Поиск событий
    @GetMapping
    public List<EventDto> getEvents(@RequestParam(required = false) List<String> users,
                                    @RequestParam(required = false) List<String> states,
                                    @RequestParam(required = false) List<String> categories,
                                    @RequestParam(required = false) String rangeStart,
                                    @RequestParam(required = false) String rangeEnd,
                                    @RequestParam(defaultValue = "0") String from,
                                    @RequestParam(defaultValue = "10") String size) {
        log.info("---> Получен запрос GET admin/events");
        return eventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
