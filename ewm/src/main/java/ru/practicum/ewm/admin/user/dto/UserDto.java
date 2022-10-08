package ru.practicum.ewm.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserDto {
    String email;
    String name;
    Long id;
}
