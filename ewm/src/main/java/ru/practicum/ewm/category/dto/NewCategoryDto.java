package ru.practicum.ewm.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Data
public class NewCategoryDto {
    @NotBlank
    @NotEmpty
    @Size(max = 255)
    String name;
}
