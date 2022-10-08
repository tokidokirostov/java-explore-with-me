package ru.practicum.ewm.user.request.controller;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.user.request.dto.RequestDto;
import ru.practicum.ewm.user.request.service.RequestService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/users")
@AllArgsConstructor
public class RequestController {
    @Autowired
    private final RequestService requestService;

    //Добавление запроса от текущего пользователя на участие в событии
    @PostMapping("/{userId}/requests")
    public RequestDto addRequest(@PathVariable(name = "userId") String userId,
                                 @RequestParam String eventId) {
        log.info("---> Получен запрос POST /users/{}/requests?eventId={}", userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    //Отмена своего запроса на участие в событии
    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable(name = "userId") String userId,
                                    @PathVariable(name = "requestId") String requestId) {
        log.info("---> Получен запрос PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    @GetMapping("{userId}/requests")
    public List<RequestDto> getRequests(@PathVariable(name = "userId") String userId) {
        log.info("---> Получен запрос GET /users/{}/requests", userId);
        return requestService.getRequests(userId);
    }
}
