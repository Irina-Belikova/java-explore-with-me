package ru.practicum.ewm.compilation.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCompilationRequest {

    @Size(min = 1, max = 50, message = "Длина заголовка подборки должна быть в пределах 1 - 50 символов.")
    private String title;

    private List<Long> events;
    private boolean pinned;
}
