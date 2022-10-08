package ru.practicum.ewm.admin.compilations.dto;

import ru.practicum.ewm.admin.compilations.model.Compilation;
import ru.practicum.ewm.user.event.dto.EventShortDto;
import ru.practicum.ewm.user.event.model.Event;

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
