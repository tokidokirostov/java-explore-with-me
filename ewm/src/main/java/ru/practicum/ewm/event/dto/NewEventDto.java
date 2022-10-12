package ru.practicum.ewm.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import ru.practicum.ewm.event.model.EventLocation;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {
    @NotEmpty
    @NotBlank
    String annotation;
    @NotEmpty
    @NotBlank
    Long category;
    @NotEmpty
    @NotBlank
    String description;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    EventLocation location;
    @NotNull
    boolean paid;
    @NotEmpty
    @NotBlank
    @Size(min = 0)
    @PositiveOrZero
    int participantLimit;
    @NotNull
    boolean requestModeration;
    @NotEmpty
    @NotBlank
    String title;

}
