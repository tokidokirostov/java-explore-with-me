package ru.practicum.ewm.event.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.storage.CategoriesRepository;
import ru.practicum.ewm.client.EndpointHit;
import ru.practicum.ewm.client.StatClient;
import ru.practicum.ewm.client.ViewStats;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.EventSort;
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

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
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

    @Autowired
    private final StatClient statClient;
    protected final ObjectMapper mapper;

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

    //Получение подробной информации об опубликованном событии по его идентификатору
    public EventDto getEvent(String idS) {
        try {
            Long id = Long.parseLong(idS);
            Event event = eventStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            if (event.getState().equals("PUBLISHED")) {
                event.setViews(getStat(event.getCreatedOn(), LocalDateTime.now(), idS, true));
                return EventMapper.toEventDto(event);
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Получение событий с возможностью фильтраций
    public List<EventDto> getEventsPublic(String text, List<String> categoriesSt, String paidS, String rangeStartSt, String rangeEndSt,
                                          String onlyAvailableS, EventSort eventSort, String fromS, String sizeS) {
        try {
            Integer from = Integer.parseInt(fromS);
            Integer size = Integer.parseInt(sizeS);
            List<Long> categories = new ArrayList<>();
            if (categoriesSt != null) {
                for (String s : categoriesSt) {
                    categories.add(Long.parseLong(s));
                }
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
            Page<Event> eventList = eventStorage.searchEvents(text, categories, paid, onlyAvailable, rangeStart, rangeEnd, page);
            if (eventSort != null && eventSort.equals(EventSort.EVENT_DATE)) {
                return eventList
                        .stream()
                        .peek(event -> event.setViews(getStat(event.getCreatedOn(), LocalDateTime.now(), String.valueOf(event.getId()), true)))
                        .sorted(Comparator.comparing(Event::getEventDate))
                        .map(event -> EventMapper.toEventDto(event))
                        .collect(Collectors.toList());
            } else if (eventSort != null && eventSort.equals(EventSort.VIEWS)) {
                return eventList
                        .stream()
                        .map(this::addViews)
                        .sorted(Comparator.comparing(Event::getViews))
                        .map(event -> EventMapper.toEventDto(event))
                        .collect(Collectors.toList());
            } else return eventList.stream()
                    .map(event -> EventMapper.toEventDto(event))
                    .collect(Collectors.toList());
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    @Transactional
    public EventDto eventPublish(String eventId) {
        try {
            Long id = Long.parseLong(eventId);
            Event event = eventStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            LocalDateTime localDateTime = LocalDateTime.now();
            LocalDateTime eventDate = event.getEventDate();
            if (eventDate.plusHours(1).isAfter(localDateTime) && event.getState().equals("PENDING")) {
                event.setPublisheOn(localDateTime);
                event.setState(String.valueOf(EventState.PUBLISHED));
                return EventMapper.toEventDto(eventStorage.save(event));
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Отклонение события
    @Transactional
    public EventDto eventReject(String eventId) {
        try {
            Long id = Long.parseLong(eventId);
            Event event = eventStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            if (!event.getState().equals("PUBLISHED")) {
                event.setState(String.valueOf(EventState.CANCELED));
                return EventMapper.toEventDto(eventStorage.save(event));
            } else throw new ForbiddenError(String.format("FORBIDDEN"));
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Редактирование события
    @Transactional
    public EventDto eventPut(NewEventDto eventDto, String eventId) {
        try {
            Long id = Long.parseLong(eventId);
            Event event = eventStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            event.setAnnotation(eventDto.getAnnotation());
            event.setCategory(categoriesRepository.findById(eventDto.getCategory())
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id))));
            event.setDescription(eventDto.getDescription());
            event.setEventDate(eventDto.getEventDate());
            if (eventDto.getLocation() != null) {
                event.setLat(eventDto.getLocation().getLat());
                event.setLon(eventDto.getLocation().getLon());
            }
            event.setPaid(eventDto.isPaid());
            event.setParticipantLimit(eventDto.getParticipantLimit());
            event.setRequestModeration(eventDto.isRequestModeration());
            event.setTitle(eventDto.getTitle());
            return EventMapper.toEventDto(eventStorage.save(event));
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Поиск событий
    @Transactional
    public List<EventDto> getEvents(List<String> usersSt, List<String> states, List<String> categoriesSt,
                                    String rangeStartSt, String rangeEndSt, String from, String size) {
        try {
            Integer pageFrom = Integer.parseInt(from);
            Integer sizeUrl = Integer.parseInt(size);
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(pageFrom, sizeUrl, sort);
            List<Long> categories = new ArrayList<>();
            for (String s : categoriesSt) {
                categories.add(Long.parseLong(s));
            }
            List<Long> users = new ArrayList<>();
            for (String s : usersSt) {
                users.add(Long.parseLong(s));
            }
            LocalDateTime rangeStart = LocalDateTime.parse("1000-01-06 13:30:38", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (rangeStartSt != null) {
                rangeStart = LocalDateTime.parse(rangeStartSt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            LocalDateTime rangeEnd = LocalDateTime.parse("3000-01-06 13:30:38", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            if (rangeEndSt != null) {
                rangeEnd = LocalDateTime.parse(rangeEndSt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            return eventStorage.searchEventByAdmin(users, states, categories, rangeStart, rangeEnd, page)
                    .stream()
                    .map(event -> EventMapper.toEventDto(event))
                    .collect(Collectors.toList());
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


    public int getStat(LocalDateTime start, LocalDateTime end, String uris, Boolean unique) {
        ResponseEntity<Object[]> responseEntity = statClient.getStat(start, end, ("/events/" + uris), false);
        List<ViewStats> viewStatsList;
        viewStatsList = Arrays.stream(Objects.requireNonNull(responseEntity.getBody()))
                .map(object -> mapper.convertValue(object, ViewStats.class))

                .collect(Collectors.toList());

        if (viewStatsList.size() != 0) {
            Integer hits = viewStatsList.get(0).getHits();
            return hits;
        }

        return 0;
    }

    private Event addViews(Event event) {
        event.setViews(getStat(event.getCreatedOn(), LocalDateTime.now(), String.valueOf(event.getId()), true));
        return event;
    }

    private void checkUser(Long id) {
        userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
    }


}
