package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.ewm.event.dto.EventShortDto;

import java.util.List;

@Data
@AllArgsConstructor
public class CompilationDto {
    List<EventShortDto> events;
    Long id;
    boolean pinned;
    String title;
}
