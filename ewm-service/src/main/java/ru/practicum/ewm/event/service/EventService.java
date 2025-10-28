package ru.practicum.ewm.event.service;

import ru.practicum.ewm.event.dto.*;

import java.util.List;

public interface EventService {

    EventFullDto addEvent(NewEventDto dto, long initiatorId);

    List<EventShortDto> getUserEvents(long initiatorId, int from, int size);

    EventFullDto getUserEventById(long eventId);

    EventFullDto updateEventByInitiator(Long eventId, UpdateEventUserRequest dto);

    EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto);

    List<EventFullDto> getEventsForAdmin(EventRequestParam param);

    EventFullDto getEventById(long eventId);

    List<EventShortDto> getEventsByParam(GetEventsRequest param);
}
