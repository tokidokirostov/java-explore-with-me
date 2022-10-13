package ru.practicum.ewm.compilation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.Set;

@Data
@AllArgsConstructor
public class NewCompilationDto {
    Set<Long> events;
    @NotEmpty
    boolean pinned;
    @NotBlank
    @NotEmpty
    @Size(max = 255)
    String title;
}
