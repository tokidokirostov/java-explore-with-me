package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class NewCategoryDto {
    @NotNull
    @NotBlank
    String name;
}
