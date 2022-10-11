package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    String email;
    String name;
    Long id;
}
