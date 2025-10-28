package ru.practicum.ewm.event.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.category.repository.CategoryRepository;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.mapper.EventMapper;
import ru.practicum.ewm.event.dto.AdminStateAction;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.dto.InitiatorStateAction;
import ru.practicum.ewm.event.model.StatusState;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.user.repository.RequestRepository;
import ru.practicum.ewm.user.repository.UserRepository;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;
    private final EventMapper mapper;

    @Override
    @Transactional
    public EventFullDto addEvent(NewEventDto dto, long initiatorId) {
        User initiator = userRepository.findById(initiatorId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователь с таким id - %s не найден.", initiatorId)));
        Category category = categoryRepository.findById(dto.getCategory())
                .orElseThrow(() -> new NotFoundException(String.format("Такой категории - %s нет в списке доступных.", dto.getCategory())));
        Event event = mapper.mapToEventFromNewEventDto(dto, category, initiator);
        event.setState(StatusState.PENDING);
        event.setCreatedOn(LocalDateTime.now());
        event = eventRepository.save(event);
        return mapper.mapToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getUserEvents(long initiatorId, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Event> events = eventRepository.findByInitiatorId(initiatorId, page).getContent();

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<LocalDateTime> createdOns = events.stream().map(Event::getCreatedOn).toList();
        Map<Long, Integer> confirmedRequests = requestRepository.getMapOfCountRequests(eventIds);
        Map<Long, Long> views = getViewsForEvents(eventIds, createdOns);

        List<EventShortDto> eventsDto = mapper.mapToEventShortDtoList(events);
        eventsDto.forEach(dto -> {
            dto.setConfirmedRequests(confirmedRequests.get(dto.getId()));
            dto.setViews(views.get(dto.getId()));
        });
        return eventsDto;
    }

    @Override
    public EventFullDto getUserEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        return addConfirmedRequestAndViewsToEventFullDto(event);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByInitiator(Long eventId, UpdateEventUserRequest dto) {
        Event updateEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        mapper.updateEventFromDto(dto, updateEvent);

        if (dto.getStateAction().equals(InitiatorStateAction.CANCEL_REVIEW)) {
            updateEvent.setState(StatusState.CANCELED);
        }

        if (dto.getCategory() != null) {
            Category newCategory = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Такой категории - %s нет в списке доступных.", dto.getCategory())));
            updateEvent.setCategory(newCategory);
        }
        return mapper.mapToEventFullDto(updateEvent);
    }

    @Override
    @Transactional
    public EventFullDto updateEventByAdmin(Long eventId, UpdateEventAdminRequest dto) {
        Event updateEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        mapper.updateEventFromAdminDto(dto, updateEvent);

        if (dto.getCategory() != null) {
            Category newCategory = categoryRepository.findById(dto.getCategory())
                    .orElseThrow(() -> new NotFoundException(String.format("Такой категории - %s нет в списке доступных.", dto.getCategory())));
            updateEvent.setCategory(newCategory);
        }

        if (dto.getStateAction().equals(AdminStateAction.PUBLISH_EVENT)) {
            updateEvent.setState(StatusState.PUBLISHED);
            updateEvent.setPublishedOn(LocalDateTime.now());
        }

        if (dto.getStateAction().equals(AdminStateAction.REJECT_EVENT)) {
            updateEvent.setState(StatusState.CANCELED);
        }
        return mapper.mapToEventFullDto(updateEvent);
    }

    @Override
    public List<EventFullDto> getEventsForAdmin(EventRequestParam param) {
        List<StatusState> statusStates = null;
        if (param.getStates() != null && !param.getStates().isEmpty()) {
            statusStates = param.getStates().stream()
                    .map(StatusState::valueOf)
                    .collect(Collectors.toList());
        }

        List<Event> events = eventRepository.findEventsByParams(param.getUsers(), statusStates,
                param.getCategories(), param.getRangeStart(), param.getRangeEnd(), param.getFrom(),
                param.getSize());

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<LocalDateTime> createdOns = events.stream().map(Event::getCreatedOn).toList();
        Map<Long, Integer> confirmedRequests = requestRepository.getMapOfCountRequests(eventIds);
        Map<Long, Long> views = getViewsForEvents(eventIds, createdOns);

        List<EventFullDto> eventsDto = mapper.mapToEventFullDtoList(events);
        eventsDto.forEach(dto -> {
            dto.setConfirmedRequests(confirmedRequests.get(dto.getId()));
            dto.setViews(views.get(dto.getId()));
        });
        return eventsDto;
    }

    @Override
    public EventFullDto getEventById(long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format("События с таким id - %d не существует.", eventId)));
        return addConfirmedRequestAndViewsToEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getEventsByParam(GetEventsRequest param) {
        List<Event> events = eventRepository.getEventsForPublic(param.getState(), param.getText(),
                param.getCategories(), param.isPaid(), param.getRangeStart(), param.getRangeEnd(),
                param.getFrom(), param.getSize());

        if (param.isOnlyAvailable()) {
            events = events.stream()
                    .filter(this::isEventAvailable)
                    .toList();
        }

        List<Long> eventIds = events.stream().map(Event::getId).toList();
        List<LocalDateTime> createdOns = events.stream().map(Event::getCreatedOn).toList();
        Map<Long, Integer> confirmedRequests = requestRepository.getMapOfCountRequests(eventIds);
        Map<Long, Long> views = getViewsForEvents(eventIds, createdOns);

        List<EventShortDto> eventsDto = mapper.mapToEventShortDtoList(events);
        eventsDto.forEach(dto -> {
            dto.setConfirmedRequests(confirmedRequests.get(dto.getId()));
            dto.setViews(views.get(dto.getId()));
        });

        if (param.getSort().equals("VIEWS")) {
            eventsDto.sort((e1, e2) -> Long.compare(e2.getViews(), e1.getViews()));
        }
        return eventsDto;
    }

    private EventFullDto addConfirmedRequestAndViewsToEventFullDto(Event event) {
        int confirmedRequest = requestRepository.getCountRequestsByEventId(event.getId());

        List<String> uris = List.of("/event/" + event.getId());

        LocalDateTime start = event.getCreatedOn();
        LocalDateTime end = LocalDateTime.now();

        List<StatsResponseDto> stats = statsClient.getStats(DateFormatterUtil.formatDateToString(start),
                DateFormatterUtil.formatDateToString(end), uris, true);

        EventFullDto dto = mapper.mapToEventFullDto(event);
        dto.setConfirmedRequests(confirmedRequest);
        dto.setViews(stats.getFirst().getHits());
        return dto;
    }

    private boolean isEventAvailable(Event event) {
        long confirmedRequest = requestRepository.getCountRequestsByEventId(event.getId());
        return event.getParticipantLimit() == 0 || confirmedRequest < event.getParticipantLimit();
    }

    private Map<Long, Long> getViewsForEvents(List<Long> eventIds, List<LocalDateTime> createdOns) {
        if (eventIds.isEmpty()) return Map.of();
        List<String> uris = eventIds.stream()
                .map(id -> "/events" + id).toList();

        LocalDateTime start = createdOns.stream()
                .min(LocalDateTime::compareTo).orElse(LocalDateTime.now());
        LocalDateTime end = LocalDateTime.now();

        List<StatsResponseDto> stats = statsClient.getStats(DateFormatterUtil.formatDateToString(start),
                DateFormatterUtil.formatDateToString(end), uris, true);

        return stats.stream()
                .collect(Collectors.toMap(
                        stat -> Long.parseLong(stat.getUri().split("/")[2]),
                        stat -> stat.getHits()
                ));
    }
}
