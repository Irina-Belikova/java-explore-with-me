package ru.practicum.ewm.event.repository;

import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.StatusState;

import java.time.LocalDateTime;
import java.util.List;

public interface EventRepositoryCustom {
    List<Event> findEventsByParams(List<Long> users, List<StatusState> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);

    List<Event> getEventsForPublic(StatusState state, String text, List<Long> categories, boolean paid,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size);
}
