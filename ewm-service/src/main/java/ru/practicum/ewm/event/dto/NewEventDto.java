package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotBlank(message = "Краткое описание события должно быть заполнено.")
    @Size(min = 20, max = 2000, message = "Краткое описание должно быть в пределах 20 - 2000 символов.")
    private String annotation;

    @NotNull(message = "Id категории должен быть указан.")
    private Long category;

    @NotBlank(message = "Полное описание события должно быть заполнено.")
    @Size(min = 20, max = 7000, message = "Полное описание должно быть в пределах 20 - 7000 символов.")
    private String description;

    @NotBlank(message = "Дата события должна быть указана.")
    private String eventDate;

    @NotNull(message = "Необходимо указать место проведения события.")
    private Location location;

    private boolean paid = false;
    private int participantLimit = 0;
    private boolean requestModeration = true;

    @NotBlank(message = "Заголовок события должен быть заполнен.")
    @Size(min = 3, max = 120, message = "Длина заголовка должна быть в пределах 3 - 120 символов.")
    private String title;
}
