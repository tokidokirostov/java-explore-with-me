package ru.practicum.ewm.admin.compilations.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Set;

@Data
@AllArgsConstructor
public class NewCompilationDto {
    Set<Long> events;
    boolean pinned;
    @NotBlank
    @NotNull
    String title;
}
