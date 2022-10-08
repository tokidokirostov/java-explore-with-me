package ru.practicum.ewm.user.event.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.category.storage.CategoriesRepository;
import ru.practicum.ewm.admin.user.model.User;
import ru.practicum.ewm.admin.user.storage.UserRepository;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.dto.*;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.model.EventState;
import ru.practicum.ewm.user.event.storage.EventStorage;
import ru.practicum.ewm.user.request.dto.RequestDto;
import ru.practicum.ewm.user.request.dto.RequestMapper;
import ru.practicum.ewm.user.request.model.Request;
import ru.practicum.ewm.user.request.storage.RequestRepository;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EventService {
    @Autowired
    private final EventStorage eventStorage;
    @Autowired
    private final UserRepository userRepository;
    @Autowired
    private final CategoriesRepository categoriesRepository;
    @Autowired
    private final RequestRepository requestRepository;

    //Добавление нового события
    public EventDto addEvent(NewEventDto newEventDto, String userIdS) {
        try {
            if (newEventDto.getEventDate().plusHours(2L).isAfter(LocalDateTime.now())) {
                Long userId = Long.parseLong(userIdS);
                Category category;
                User user = userRepository.findById(userId)
                        .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", userId)));
                if (newEventDto.getCategory() == null) {
                    category = null;
                } else {
                    category = categoriesRepository.findById(newEventDto.getCategory())
                            .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.",
                                    newEventDto.getCategory())));
                }
                Event event = eventStorage.save(EventMapper.toEvent(newEventDto, user, category));
                return EventMapper.toEventDto(event);
            } else throw new ForbiddenError(String.format("FORBIDDEN"));

        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Получение событий, добавленных текущим пользователем
    public List<EventShortDto> getEventsByUser(String userIdS, String from, String size) {
        try {
            Integer pageFrom = Integer.parseInt(from);
            Integer sizeUrl = Integer.parseInt(size);
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(pageFrom, sizeUrl, sort);
            return eventStorage.findAllByInitiatorId(userId, page).stream()
                    .map(event -> EventMapper.toEventShortDto(event))
                    .collect(Collectors.toList());
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Изменение события, добавленного текущим пользователем
    //Proverit
    public EventDto patchEvent(EventUpdateDto eventUpdateDto, String userIdS) {
        try {
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Category category = categoriesRepository.findById(eventUpdateDto.getCategory())
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventUpdateDto.getCategory())));
            Optional<Event> event = eventStorage.findById(eventUpdateDto.getEventId());
            if (event.isEmpty()) {
                throw new UserNotFoundException(String.format("Event with id=%d was not found.", eventUpdateDto.getEventId()));
            }
            if (event.get().getState().equals("PUBLISHED")) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            if (event.get().getState().equals("CANCELED")) {
                event.get().setState(String.valueOf(EventState.PENDING));
            }
            if (eventUpdateDto.getEventDate().plusHours(2).isAfter(LocalDateTime.now())) {
                event.get().setAnnotation(eventUpdateDto.getAnnotation());
                event.get().setCategory(category);
                event.get().setDescription(eventUpdateDto.getDescription());
                event.get().setEventDate(eventUpdateDto.getEventDate());
                event.get().setPaid(eventUpdateDto.isPaid());
                event.get().setParticipantLimit(eventUpdateDto.getParticipantLimit());
                event.get().setTitle(eventUpdateDto.getTitle());
                return EventMapper.toEventDto(eventStorage.save(event.get()));
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    //Получение полной информации о событии добавленном текущим пользователем
    public EventDto getEventByUser(String userIdS, String eventIdS) {
        try {
            Long userId = Long.parseLong(userIdS);
            Long eventId = Long.parseLong(eventIdS);
            checkUser(userId);
            return EventMapper.toEventDto(eventStorage.findByIdAndInitiatorId(eventId, userId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventId))));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Отмена события добавленного текщим пользователем
    public EventDto canselEvent(String userIdS, String eventIdS) {
        try {
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Long eventId = Long.parseLong(eventIdS);
            Event event = eventStorage.findByIdAndInitiatorId(eventId, userId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", userId)));
            if (event.getState().equals("PENDING")) {
                event.setState(String.valueOf(EventState.CANCELED));
                return EventMapper.toEventDto(eventStorage.save(event));
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Получение информации о запросах на участие в событии текущего пользователя
    @Transactional
    public List<RequestDto> getEventByUserRequests(String userIdS, String eventIdS, String from, String size) {
        try {
            Long eventId = Long.parseLong(eventIdS);
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Integer pageFrom = Integer.parseInt(from);
            Integer sizeUrl = Integer.parseInt(size);
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(pageFrom, sizeUrl, sort);
            return requestRepository.findAllByEventId(eventId, page)
                    .stream()
                    .map(request -> RequestMapper.toRequestDto(request))
                    .collect(Collectors.toList());
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    //Подтверждение чужой заявки на участие в событии текущего пользователя
    @Transactional
    public RequestDto confirmEventRequest(String userIdS, String eventIdS, String requestIdS) {
        try {
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Long eventId = Long.parseLong(eventIdS);
            Long requestId = Long.parseLong(requestIdS);
            checkUser(requestId);
            Event event = eventStorage.findById(eventId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventId)));
            if (!event.getInitiator().getId().equals(userId)) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", requestId)));
            if (event.getParticipantLimit() == 0 || !event.isRequestModeration()) {
                return RequestMapper.toRequestDto(request);
            }
            if (event.getParticipantLimit() == event.getConfirmedRequests()) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            request.setStatus(EventState.CONFIRMED);
            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            Event confitmedEvent = eventStorage.save(event);
            if (confitmedEvent.getParticipantLimit() == confitmedEvent.getConfirmedRequests()) {
                List<Request> requestList = requestRepository.findAllByEventIdAndRequesterId(eventId, requestId);
                for (Request request1 : requestList) {
                    request1.setStatus(EventState.REJECTED);
                    requestRepository.save(request1);
                }
            }
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }

    //Отклонение чужой заявки на участие в событии текущего пользователя
    public RequestDto rejectEventRequest(String userIdS, String eventIdS, String requestIdS) {
        try {
            Long userId = Long.parseLong(userIdS);
            checkUser(userId);
            Long eventId = Long.parseLong(eventIdS);
            Long requestId = Long.parseLong(requestIdS);
            checkUser(requestId);
            Event event = eventStorage.findById(eventId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", eventId)));
            if (!event.getInitiator().getId().equals(userId)) {
                throw new ForbiddenError(String.format("FORBIDDEN"));
            }
            Request request = requestRepository.findById(requestId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", requestId)));
            request.setStatus(EventState.REJECTED);
            return RequestMapper.toRequestDto(requestRepository.save(request));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    private void checkUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
    }
}
