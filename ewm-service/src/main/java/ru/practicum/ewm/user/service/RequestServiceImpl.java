package ru.practicum.ewm.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.mapper.RequestMapper;
import ru.practicum.ewm.user.model.Request;
import ru.practicum.ewm.user.model.RequestStatus;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper mapper;

    @Override
    @Transactional
    public ParticipationRequestDto addRequest(Long requesterId, Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        User requester = userRepository.findById(requesterId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", requesterId)));
        Request request = new Request();
        request.setEvent(event);
        request.setRequester(requester);
        request.setCreated(LocalDateTime.now());

        if (!event.isRequestModeration()) {
            request.setStatus(RequestStatus.CONFIRMED);
        } else {
            request.setStatus(RequestStatus.PENDING);
        }

        request = requestRepository.save(request);
        return mapper.mapToRequestDto(request);
    }

    @Override
    @Transactional
    public ParticipationRequestDto deleteOwnRequest(long requestId) {
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с id -%d не существует.", requestId)));
        request.setStatus(RequestStatus.CANCELED);
        return mapper.mapToRequestDto(request);
    }

    @Override
    public List<ParticipationRequestDto> getOwnRequests(long requesterId) {
        List<Request> requests = requestRepository.findByRequesterId(requesterId);
        return mapper.mapToRequestDtoList(requests);
    }

    @Override
    public List<ParticipationRequestDto> getRequestsByEventId(long eventId) {
        List<Request> requests = requestRepository.findByEventId(eventId);
        return mapper.mapToRequestDtoList(requests);
    }

    @Override
    @Transactional
    public EventRequestStatusUpdateResult updateRequestsByEventId(long eventId, EventRequestStatusUpdateRequest dto) {
        List<Request> requests = requestRepository.findByIdIn(dto.getRequestIds());
        List<Request> confirmedRequests = new ArrayList<>();
        List<Request> rejectedRequests = new ArrayList<>();
        long participantLimit = eventRepository.getParticipantLimit(eventId);

        if (participantLimit == 0) {
            requests.forEach(request -> {
                request.setStatus(RequestStatus.CONFIRMED);
                confirmedRequests.add(request);
            });
        } else {
            int confirmedCount = requestRepository.getCountRequestsByEventId(eventId);
            for (Request request : requests) {
                if (confirmedCount == participantLimit) {
                    request.setStatus(RequestStatus.CANCELED);
                    rejectedRequests.add(request);
                } else {
                    request.setStatus(RequestStatus.CONFIRMED);
                    confirmedRequests.add(request);
                    confirmedCount = confirmedCount + 1;
                }
            }
        }
        List<ParticipationRequestDto> confirmedDto = mapper.mapToRequestDtoList(confirmedRequests);
        List<ParticipationRequestDto> rejectedDto = mapper.mapToRequestDtoList(rejectedRequests);
        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedDto)
                .rejectedRequests(rejectedDto)
                .build();
    }
}
