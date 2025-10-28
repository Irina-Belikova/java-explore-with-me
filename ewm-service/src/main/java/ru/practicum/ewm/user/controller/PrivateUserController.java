package ru.practicum.ewm.user.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.NewEventDto;
import ru.practicum.ewm.event.dto.UpdateEventUserRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateRequest;
import ru.practicum.ewm.user.dto.EventRequestStatusUpdateResult;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.service.RequestService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.util.List;

@RestController
@RequestMapping("/users/{userId}")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PrivateUserController {
    private final EventService eventService;
    private final RequestService requestService;
    private final ValidationUtil validation;

    @PostMapping("/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        log.info("Поступил запрос от пользователя - {} на добавление события: {}", userId, dto);
        validation.validationForAddEvent(dto);
        return eventService.addEvent(dto, userId);
    }

    @GetMapping("/events")
    @ResponseStatus(code = HttpStatus.OK)
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(required = false, defaultValue = "0")
                                             @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                             @RequestParam(required = false, defaultValue = "10")
                                             @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Поступил запрос от пользователя с id - {} на получение списка своих событий с from - {} и size - {}",
                userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getUserEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Поступил запрос от пользователя - {} на получение информации о его событии - {}.", userId, eventId);
        validation.validationGetUserEventById(userId, eventId);
        return eventService.getUserEventById(eventId);
    }

    @PatchMapping("/events/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto updateEventByInitiator(@PathVariable Long userId, @PathVariable Long eventId,
                                               @Valid @RequestBody UpdateEventUserRequest dto) {
        log.info("Запрос от пользователя - {} на изменение события - {}, {}", userId, eventId, dto);
        validation.validationUpdateEventByInitiator(userId, eventId, dto);
        return eventService.updateEventByInitiator(eventId, dto);
    }

    @PostMapping("/requests")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ParticipationRequestDto addRequest(@PathVariable Long userId, @RequestParam Long eventId) {
        log.info("Запрос на создание заявки от пользователя - {}, событие - {}", userId, eventId);
        validation.validationAddRequest(userId, eventId);
        return requestService.addRequest(userId, eventId);
    }

    @PatchMapping("/requests/{requestId}/cancel")
    @ResponseStatus(code = HttpStatus.OK)
    public ParticipationRequestDto deleteOwnRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Запрос от пользователя - {} на отмену своего участия в событии - {}.", userId, requestId);
        validation.validationDeleteOwnRequest(userId, requestId);
        return requestService.deleteOwnRequest(requestId);
    }

    @GetMapping("/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipationRequestDto> getOwnRequests(@PathVariable Long userId) {
        log.info("Запрос от пользователя - {} на получение списка событий, в которых он участвует.", userId);
        validation.checkUserId(userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос от пользователя - {} на получения списка заявок на событие - {}.", userId, eventId);
        validation.validationGetUserEventById(userId, eventId);
        return requestService.getRequestsByEventId(eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    @ResponseStatus(code = HttpStatus.OK)
    public EventRequestStatusUpdateResult updateRequestsByEventId(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Запрос пользователя - {} на изменение статуса заявок на его событие - {}.", userId, eventId);
        validation.validationUpdateRequestsByEventId(userId, eventId, dto);
        return requestService.updateRequestsByEventId(eventId, dto);
    }
}
