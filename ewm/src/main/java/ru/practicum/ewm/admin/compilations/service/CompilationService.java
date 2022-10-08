package ru.practicum.ewm.admin.compilations.service;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.admin.compilations.dto.CompilationDto;
import ru.practicum.ewm.admin.compilations.dto.CompilationMapper;
import ru.practicum.ewm.admin.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.admin.compilations.model.Compilation;
import ru.practicum.ewm.admin.compilations.storage.CompilationStorage;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;
import ru.practicum.ewm.user.event.dto.EventMapper;
import ru.practicum.ewm.user.event.dto.EventShortDto;
import ru.practicum.ewm.user.event.model.Event;
import ru.practicum.ewm.user.event.storage.EventStorage;

import javax.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CompilationService {
    @Autowired
    private final CompilationStorage compilationStorage;
    @Autowired
    private final EventStorage eventStorage;

    //Добавление подборки
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        List<Event> eventList = eventStorage.findAllById(newCompilationDto.getEvents());
        Compilation compilation = compilationStorage.save(CompilationMapper.toCompilation(newCompilationDto, eventList));
        List<EventShortDto> events = eventList
                .stream()
                .map(event -> EventMapper.toEventShortDto(event))
                .collect(Collectors.toList());
        return CompilationMapper.toCompilationDto(compilation, events);
    }

    //Удаление подборки
    @Transactional
    public void deleteCompilation(String compId) {
        try {
            Long id = Long.parseLong(compId);
            if (compilationStorage.findById(id).isEmpty()) {
                throw new UserNotFoundException(String.format("Event with id=%d was not found.", id));
            } else {
                compilationStorage.deleteById(id);
            }
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError("FORBIDDEN");
        }
    }

    //Удаление события из подборки
    @Transactional
    public void deleteEventFromCompilation(String compId, String eventId) {
        try {
            Long id = Long.parseLong(compId);
            Long eId = Long.parseLong(eventId);
            Compilation compilation = compilationStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
                List<Event> eventList = compilation.getEvents();
                eventList.removeIf(e -> e.getId() == eId);
                compilation.setEvents(eventList);
                compilationStorage.save(compilation);
        } catch (NumberFormatException numberFormatException) {
            throw new RequestError(String.format("FORBIDDEN"));
        }
    }

    //Добавить событие в подборку
    @Transactional
    public void addEventToCompilation(String compId, String eventId) {
        try {
            Long id = Long.parseLong(compId);
            Long eId = Long.parseLong(eventId);
            Compilation compilation = compilationStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            Event event = eventStorage.findById(eId)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            compilation.getEvents().add(event);
            compilationStorage.save(compilation);
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError("FORBIDDEN");
        }
    }

    //Закрепить подборку на главной странице
    @Transactional
    public void addCompilationOnBoard(String compId) {
        try {
            Long id = Long.parseLong(compId);
            Compilation compilation = compilationStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            compilation.setPinned(true);
            compilationStorage.save(compilation);
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError("FORBIDDEN");
        }

    }

    //Открепить подборку на главной странице
    @Transactional
    public void deleteCompilationOnBoard(String compId) {
        try {
            Long id = Long.parseLong(compId);
            Compilation compilation = compilationStorage.findById(id)
                    .orElseThrow(() -> new UserNotFoundException(String.format("Event with id=%d was not found.", id)));
            compilation.setPinned(false);
            compilationStorage.save(compilation);
        } catch (
                NumberFormatException numberFormatException) {
            throw new RequestError("FORBIDDEN");
        }
    }
}
