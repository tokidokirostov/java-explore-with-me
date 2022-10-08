package ru.practicum.ewm.admin.event.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.admin.event.service.AdminEventService;
import ru.practicum.ewm.user.event.dto.EventDto;
import ru.practicum.ewm.user.event.dto.NewEventDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/admin/events")
@AllArgsConstructor
public class AdminEventController {
    @Autowired
    private final AdminEventService adminEventService;

    //Публикация события
    @PatchMapping("{eventId}/publish")
    public EventDto eventPublish(@PathVariable String eventId) {
        log.info("---> Получен запрос PATCH admin/events/{}/publish", eventId);
        return adminEventService.eventPublish(eventId);
    }

    //Отклонение события
    @PatchMapping("{eventId}/reject")
    public EventDto eventReject(@PathVariable String eventId) {
        log.info("---> Получен запрос PATCH admin/events/{}/publish", eventId);
        return adminEventService.eventReject(eventId);
    }

    //Редактирование события
    @PutMapping("{eventId}")
    public EventDto eventPut(@RequestBody NewEventDto eventDto,
                             @PathVariable String eventId) {
        log.info("---> Получен запрос PUT admin/events/{} - NewEvent {}", eventId, eventDto);
        return adminEventService.eventPut(eventDto, eventId);
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
        return adminEventService.getEvents(users, states, categories, rangeStart, rangeEnd, from, size);
    }
}
