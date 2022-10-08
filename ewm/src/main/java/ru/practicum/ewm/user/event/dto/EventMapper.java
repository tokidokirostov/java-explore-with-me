package ru.practicum.ewm.user.event.dto;

import ru.practicum.ewm.admin.category.dto.CategoryMapper;
import ru.practicum.ewm.admin.category.model.Category;
import ru.practicum.ewm.admin.user.dto.UserMapper;
import ru.practicum.ewm.admin.user.model.User;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.model.EventLocation;
import ru.practicum.ewm.user.event.model.EventState;

import java.time.LocalDateTime;

public class EventMapper {

    public static Event toEvent(NewEventDto newEventDto, User user, Category category) {
        return new Event(
                null,
                newEventDto.getAnnotation(),
                category,
                0,
                LocalDateTime.now(),
                newEventDto.getDescription(),
                newEventDto.getEventDate(),
                user,
                newEventDto.isPaid(),
                newEventDto.getParticipantLimit(),
                null,
                newEventDto.isRequestModeration(),
                EventState.PENDING.toString(),
                newEventDto.getTitle(),
                0,
                newEventDto.getLocation().getLat(),
                newEventDto.getLocation().getLon());

    }

    public static EventDto toEventDto(Event event) {
        return new EventDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                new EventLocation(event.getLat(), event.getLon()),
                event.isPaid(),
                event.getParticipantLimit(),
                event.getPublisheOn(),
                event.isRequestModeration(),
                EventState.valueOf(event.getState()),
                event.getTitle(),
                event.getViews());
    }

    public static EventShortDto toEventShortDto(Event event) {
        return new EventShortDto(
                event.getAnnotation(),
                CategoryMapper.toCategoryDto(event.getCategory()),
                event.getConfirmedRequests(),
                event.getEventDate(),
                event.getId(),
                UserMapper.toUserShortDto(event.getInitiator()),
                event.isPaid(),
                event.getTitle(),
                event.getViews());
    }

}
