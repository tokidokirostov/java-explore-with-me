package ru.practicum.ewm.admin.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
public class UserCreateDto {
    Long id;
    @NonNull
    @Email
    String email;
    @NotBlank
    @NonNull
    String name;
}
