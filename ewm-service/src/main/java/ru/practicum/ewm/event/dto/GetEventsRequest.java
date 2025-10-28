package ru.practicum.ewm.event.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.ewm.event.model.StatusState;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class GetEventsRequest {
    private StatusState state;
    private String text;
    private List<Long> categories;
    private boolean paid;
    private LocalDateTime rangeStart;
    private LocalDateTime rangeEnd;
    private boolean onlyAvailable;
    private String sort;
    private int from;
    private int size;
}
