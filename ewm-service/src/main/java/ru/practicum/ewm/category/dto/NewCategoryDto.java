package ru.practicum.ewm.category.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCategoryDto {

    @NotBlank(message = "Название пользователя не может быть пустым.")
    @Size(min = 1, max = 50, message = "Название категории должно быть в пределах 1-50 символов.")
    private String name;
}
