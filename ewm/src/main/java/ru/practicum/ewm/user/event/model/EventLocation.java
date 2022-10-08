package ru.practicum.ewm.user.event.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EventLocation {
    float lat;
    float lon;
}
