package ru.practicum.ewm.user.event.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.event.dto.EventDto;
import ru.practicum.ewm.user.event.dto.EventShortDto;
import ru.practicum.ewm.user.event.dto.EventUpdateDto;
import ru.practicum.ewm.user.event.dto.NewEventDto;
import ru.practicum.ewm.user.event.service.EventService;
import ru.practicum.ewm.user.request.dto.RequestDto;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
public class EventController {
    @Autowired
    EventService eventService;

    //Добавление нового события
    @PostMapping("/{userId}/events")
    public EventDto addEvent(@RequestBody NewEventDto newEventDto,
                             @PathVariable String userId) {
        log.info("---> Получен запрос POST /users/{}/events event - {}", userId, newEventDto.toString());
        return eventService.addEvent(newEventDto, userId);
    }

    //Получение событий, добавленных текущим пользователем
    @GetMapping("/{userId}/events")
    public List<EventShortDto> getEventsByUser(@PathVariable String userId,
                                               @RequestParam(defaultValue = "0") String from,
                                               @RequestParam(defaultValue = "10") String size) {
        log.info("---> Получен запрос GET /users/{}/events ", userId);
        return eventService.getEventsByUser(userId, from, size);
    }

    //Изменение события, добавленного текущим пользователем
    @PatchMapping("/{userId}/events")
    public EventDto patchEvent(@RequestBody EventUpdateDto eventUpdateDto,
                               @PathVariable String userId) {
        log.info("---> Получен запрос POST /users/{}/events event - {}", userId, eventUpdateDto.toString());
        return eventService.patchEvent(eventUpdateDto, userId);
    }

    //Получение полной информации о событии добавленном текущим пользователем
    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getEventByUser(@PathVariable String userId,
                                   @PathVariable String eventId) {
        log.info("---> Получен запрос GET /users/{}/events/{} ", userId, eventId);
        return eventService.getEventByUser(userId, eventId);
    }

    //Отмена события добавленного текщим пользователем
    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto canselEvent(@PathVariable String userId,
                                @PathVariable String eventId) {
        log.info("---> Получен запрос PATCH /users/{}/events/{} ", userId, eventId);
        return eventService.canselEvent(userId, eventId);
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<RequestDto> getEventsByUserRequests(@PathVariable String userId,
                                                    @PathVariable String eventId,
                                                    @RequestParam(defaultValue = "0") String from,
                                                    @RequestParam(defaultValue = "10") String size) {
        log.info("---> Получен запрос GET /users/{}/events/{}/requests ", userId, eventId);
        return eventService.getEventByUserRequests(userId, eventId, from, size);
    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{requestId}/confirm")
    public RequestDto confirmEventRequest(@PathVariable String userId,
                                          @PathVariable String eventId,
                                          @PathVariable String requestId) {
        log.info("---> Получен запрос PATCH /users/{}/events/{}/requests/{}/confirm ", userId, eventId, requestId);
        return eventService.confirmEventRequest(userId, eventId, requestId);
    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    @PatchMapping("/{userId}/events/{eventId}/requests/{requestId}/reject")
    public RequestDto rejectEventRequest(@PathVariable String userId,
                                         @PathVariable String eventId,
                                         @PathVariable String requestId) {
        log.info("---> Получен запрос PATCH /users/{}/events/{}/requests/{}/reject ", userId, eventId, requestId);
        return eventService.rejectEventRequest(userId, eventId, requestId);
    }
}
