package ru.practicum.ewm.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventRequestStatusUpdateRequest {

    @NotNull(message = "Список id запросов не может быть пустым.")
    private List<Long> requestIds;

    @NotNull(message = "Необходимо указать статус, присваиваемый заявкам.")
    private UpdateRequestStaus status;
}
