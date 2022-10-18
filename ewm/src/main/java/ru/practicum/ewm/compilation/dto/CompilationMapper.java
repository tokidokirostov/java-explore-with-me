package ru.practicum.ewm.compilation.dto;

import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.model.Event;

import java.util.List;

public class CompilationMapper {
    public static CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> events) {
        return new CompilationDto(
                events,
                compilation.getId(),
                compilation.isPinned(),
                compilation.getTitle()
        );
    }

    public static Compilation toCompilation(NewCompilationDto newCompilationDto, List<Event> eventList) {
        return new Compilation(
                null,
                newCompilationDto.getTitle(),
                newCompilationDto.isPinned(),
                eventList);
    }
}
