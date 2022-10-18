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
    @Size(max = 400)
    String annotation;
    @NotNull
    Long category;
    @NotEmpty
    @NotBlank
    @Size(max = 4000)
    String description;
    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE, pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime eventDate;
    @NotNull
    EventLocation location;
    @NotEmpty
    boolean paid;
    @NotEmpty
    @PositiveOrZero
    int participantLimit;
    @NotEmpty
    boolean requestModeration;
    @NotEmpty
    @NotBlank
    @Size(max = 255)
    String title;

}
