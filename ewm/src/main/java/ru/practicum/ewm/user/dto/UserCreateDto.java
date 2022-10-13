package ru.practicum.ewm.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
public class UserCreateDto {
    Long id;
    @NotEmpty
    @NotBlank
    @Size(max = 255)
    @Email
    String email;
    @NotBlank
    @NotEmpty
    @Size(max = 255)
    String name;
}
