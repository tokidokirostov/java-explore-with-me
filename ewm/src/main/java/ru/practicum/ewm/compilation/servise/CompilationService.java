package ru.practicum.ewm.compilation.servise;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.CompilationMapper;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.storage.CompilationStorage;
import ru.practicum.ewm.event.dto.EventMapper;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.storage.EventStorage;
import ru.practicum.ewm.exception.RequestError;
import ru.practicum.ewm.exception.UserNotFoundException;

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
            eventList.removeIf(e -> e.getId().equals(eId));
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
            List<Event> eventList = compilation.getEvents();
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
