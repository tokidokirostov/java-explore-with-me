package ru.practicum.ewm.admin.event.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.category.storage.CategoriesRepository;
import ru.practicum.ewm.exception.ForbiddenError;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.dto.EventDto;
import ru.practicum.ewm.user.event.dto.EventMapper;
import ru.practicum.ewm.user.event.dto.NewEventDto;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.model.EventState;
import ru.practicum.ewm.user.event.storage.EventStorage;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminEventService {
    private final EventStorage eventStorage;
    private final CategoriesRepository categoriesRepository;

    //Публикация события
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

}
