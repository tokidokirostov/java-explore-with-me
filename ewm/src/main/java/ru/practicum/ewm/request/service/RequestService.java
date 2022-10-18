package ru.practicum.ewm.request.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.event.storage.EventStorage;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.request.dto.RequestDto;
import ru.practicum.ewm.request.dto.RequestMapper;
import ru.practicum.ewm.request.model.Request;
import ru.practicum.ewm.request.storage.RequestRepository;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.storage.UserRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RequestService {
    @Autowired
    private final RequestRepository requestRepository;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final EventStorage eventStorage;

    //Добавление запроса от текущего пользователя на участие в событии
    @Transactional
    public RequestDto addRequest(String userId, String eventIdS) {
        try {
            Long requesterId = Long.parseLong(userId);
            User user = userRepository.findById(requesterId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", requesterId)));
            Long eventId = Long.parseLong(eventIdS);
            Event event = eventStorage.findById(eventId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventId)));
            Optional<Request> requestSearch = requestRepository.findByRequesterIdAndEventId(requesterId, eventId);
            if (requestSearch.isPresent() || event.getInitiator().getId().equals(requesterId) || !event.getState().equals("PUBLISHED")) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            if (event.getParticipantLimit() != 0) {
                if (requestRepository.countAllByEventId(eventId) >= event.getParticipantLimit()) {
                    throw new ForbiddenError(String.format("FORBIDDEN"));
                }
            }
            Request request = new Request(LocalDateTime.now(),
                    event, null, user, EventState.PENDING);
            if (!event.isRequestModeration()) {
                request.setStatus(EventState.PUBLISHED);
            }
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    //Получение информации о заявках текущего пользователя на участие в чужих событиях
    public List<RequestDto> getRequests(String userId) {
        try {
            Long id = Long.parseLong(userId);
            return requestRepository.findAllByRequesterId(id)
                    .stream()
                    .map(request -> RequestMapper.toRequestDto(request))
                    .collect(Collectors.toList());
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Отмена своего запроса на участие в событии
    @Transactional
    public RequestDto cancelRequest(String userId, String requestId) {
        try {
            Long id = Long.parseLong(userId);
            Long idR = Long.parseLong(requestId);
            Request request = requestRepository.findByIdAndRequesterId(idR, id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            request.setStatus(EventState.CANCELED);
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }
}
