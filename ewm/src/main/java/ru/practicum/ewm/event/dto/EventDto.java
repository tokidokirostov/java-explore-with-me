package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.category.dto.CategoryDto;
import ru.practicum.ewm.event.model.EventLocation;
import ru.practicum.ewm.event.model.EventState;
import ru.practicum.ewm.user.dto.UserShortDto;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class EventDto {
    //Краткое описание
    String annotation;
    //Long category_id;
    CategoryDto category;
    //Количество одобренных заявок на участие в данном событии
    int confirmedRequests;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime createdOn;
    String description;
    //Дата и время на которые намечено событие (в формате "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    Long id;
    UserShortDto initiator;
    EventLocation location;
    boolean paid;
    //Ограничение на количество участников. Значение 0 - означает отсутствие ограничения
    int participantLimit;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime publishedOn;
    //Нужна ли пре-модерация заявок на участие
    boolean requestModeration;
    EventState state;
    String title;
    int views;
}
