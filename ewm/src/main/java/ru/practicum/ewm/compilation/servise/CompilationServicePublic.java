package ru.practicum.ewm.compilation.servise;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.CompilationMapper;
import ru.practicum.ewm.admin.compilations.model.Compilation;
import ru.practicum.ewm.admin.compilations.storage.CompilationStorage;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.dto.EventMapper;
import ru.practicum.ewm.user.event.dto.EventShortDto;
import ru.practicum.ewm.user.event.model.Event;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationServicePublic {
    @Autowired
    private final CompilationStorage compilationStorage;

    //Получение подборок событий
    public List<CompilationDto> getAllCompilations(String pinnedS, String fromS, String sizeS) {
        try {
            Boolean pinned = Boolean.parseBoolean(pinnedS);
            Integer from = Integer.parseInt(fromS);
            Integer size = Integer.parseInt(sizeS);
            Sort sort = Sort.unsorted();
            Pageable page = PageRequest.of(from, size, sort);
            return compilationStorage.searchAll(pinned, page)
                    .stream()
                    .map(compilation ->
                            CompilationMapper.toCompilationDto(compilation, compilation.getEvents()
                                    .stream()
                                    .map(event -> EventMapper.toEventShortDto(event))
                                    .collect(Collectors.toList()))
                    )
                    .collect(Collectors.toList());
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Получение подборки событий по его id
    public CompilationDto getCompilation(String idS) {
        try {
            Long id = Long.parseLong(idS);
            Compilation compilation = compilationStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            System.out.println(compilation);
            List<Event> eventList = compilation.getEvents();
            System.out.println(eventList);
            List<EventShortDto> eventsShortDto = eventList
                    .stream()
                    .map(event -> EventMapper.toEventShortDto(event))
                    .collect(Collectors.toList());
            return CompilationMapper.toCompilationDto(compilation, eventsShortDto);
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }

    }
}
