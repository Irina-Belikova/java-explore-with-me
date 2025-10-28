package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewCompilationDto {

    @NotBlank(message = "Заголовок подборки не может быть пустым.")
    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть в пределах 1 - 50 символов.")
    private String title;

    private List<Long> events = new ArrayList<>();
    private boolean pinned = false;
}
