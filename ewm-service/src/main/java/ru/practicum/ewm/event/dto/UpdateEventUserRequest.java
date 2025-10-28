package ru.practicum.ewm.event.dto;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.event.model.Location;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEventUserRequest {

    @Size(min = 3, max = 120, message = "Длина заголовка должна быть в пределах 3 - 120 символов.")
    private String title;

    @Size(min = 20, max = 2000, message = "Краткое описание должно быть в пределах 20 - 2000 символов.")
    private String annotation;

    @Size(min = 20, max = 7000, message = "Полное описание должно быть в пределах 20 - 7000 символов.")
    private String description;

    private String eventDate;
    private Location location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private Long category;
    private InitiatorStateAction stateAction;
}
