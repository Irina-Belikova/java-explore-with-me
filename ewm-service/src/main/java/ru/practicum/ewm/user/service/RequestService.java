package ru.practicum.ewm.user.service;

import ru.practicum.ewm.user.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {

    ParticipationRequestDto addRequest(Long requesterId, Long evetId);

    ParticipationRequestDto deleteOwnRequest(long requestId);

    List<ParticipationRequestDto> getOwnRequests(long requesterId);

    List<ParticipationRequestDto> getRequestsByEventId(long eventId);

    EventRequestStatusUpdateResult updateRequestsByEventId(long eventId, EventRequestStatusUpdateRequest dto);
}
