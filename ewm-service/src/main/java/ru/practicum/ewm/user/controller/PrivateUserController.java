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
import ru.practicum.ewm.user.dto.*;
import ru.practicum.ewm.user.service.CommentService;
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
    private final CommentService commentService;
    private final ValidationUtil validation;

    @PostMapping("/events")
    @ResponseStatus(code = HttpStatus.CREATED)
    public EventFullDto addEvent(@PathVariable Long userId, @Valid @RequestBody NewEventDto dto) {
        log.info("Поступил запрос от пользователя - {} на добавление события: {}", userId, dto);
        validation.validationForAddEvent(dto);
        return eventService.addEvent(dto, userId);
    }

    @GetMapping("/events")
    public List<EventShortDto> getUserEvents(@PathVariable Long userId,
                                             @RequestParam(defaultValue = "0")
                                             @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                             @RequestParam(defaultValue = "10")
                                             @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Поступил запрос от пользователя с id - {} на получение списка своих событий с from - {} и size - {}",
                userId, from, size);
        return eventService.getUserEvents(userId, from, size);
    }

    @GetMapping("/events/{eventId}")
    public EventFullDto getUserEventById(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Поступил запрос от пользователя - {} на получение информации о его событии - {}.", userId, eventId);
        validation.validationGetUserEventById(userId, eventId);
        return eventService.getUserEventById(eventId);
    }

    @PatchMapping("/events/{eventId}")
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
    public ParticipationRequestDto deleteOwnRequest(@PathVariable Long userId, @PathVariable Long requestId) {
        log.info("Запрос от пользователя - {} на отмену своего участия в событии - {}.", userId, requestId);
        validation.validationDeleteOwnRequest(userId, requestId);
        return requestService.deleteOwnRequest(requestId);
    }

    @GetMapping("/requests")
    public List<ParticipationRequestDto> getOwnRequests(@PathVariable Long userId) {
        log.info("Запрос от пользователя - {} на получение списка событий, в которых он участвует.", userId);
        validation.checkUserId(userId);
        return requestService.getOwnRequests(userId);
    }

    @GetMapping("/events/{eventId}/requests")
    public List<ParticipationRequestDto> getRequestsByEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        log.info("Запрос от пользователя - {} на получения списка заявок на событие - {}.", userId, eventId);
        validation.validationGetUserEventById(userId, eventId);
        return requestService.getRequestsByEventId(eventId);
    }

    @PatchMapping("/events/{eventId}/requests")
    public EventRequestStatusUpdateResult updateRequestsByEventId(@PathVariable Long userId,
                                                                  @PathVariable Long eventId,
                                                                  @Valid @RequestBody EventRequestStatusUpdateRequest dto) {
        log.info("Запрос пользователя - {} на изменение статуса заявок на его событие - {}.", userId, eventId);
        validation.validationUpdateRequestsByEventId(userId, eventId, dto);
        return requestService.updateRequestsByEventId(eventId, dto);
    }

    @PostMapping("/comments")
    @ResponseStatus(code = HttpStatus.CREATED)
    public CommentShortResponse addComment(@PathVariable Long userId,
                                           @RequestParam Long eventId, @Valid @RequestBody CommentDto dto) {
        log.info("Запрос на создание комментария от пользователя - {}, на событие - {}", userId, eventId);
        validation.validationAddComment(eventId);
        return commentService.addComment(userId, eventId, dto);
    }

    @PostMapping("/comments/{commentId}/replies")
    @ResponseStatus(code = HttpStatus.CREATED)
    public ReplyCommentResponse replyToComment(@PathVariable Long userId, @PathVariable Long commentId,
                                               @Valid @RequestBody CommentDto dto) {
        log.info("Запрос на ответ на комментарий - {} от пользователя {}.", commentId, userId);
        validation.checkCommentId(commentId);
        return commentService.replyToComment(userId, commentId, dto);
    }

    @PatchMapping("/comments/{commentId}")
    public CommentShortResponse updateComment(@PathVariable Long userId, @PathVariable Long commentId,
                                              @Valid @RequestBody CommentDto dto) {
        log.info("Запрос от пользователя - {} на изменение своего комментария - {}", userId, commentId);
        validation.validationUpdateComment(userId, commentId);
        return commentService.updateComment(commentId, dto);
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteOwnComment(@PathVariable Long userId, @PathVariable Long commentId) {
        log.info("Запрос от пользователя - {} на удаление своего комментария - {}.", userId, commentId);
        validation.validationDeleteOwnComment(userId, commentId);
        commentService.deleteOwnComment(commentId);
    }

    @GetMapping("/comments/{commentId}")
    public List<ReplyCommentResponse> getReplyComments(@PathVariable Long userId, @PathVariable Long commentId,
                                                       @RequestParam(defaultValue = "ASC") String sort,
                                                       @RequestParam(defaultValue = "0")
                                                       @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                                       @RequestParam(defaultValue = "10")
                                                       @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Запрос на получение списка ответов на свой комментарий - {}", commentId);
        validation.validationGetReplyComments(userId, commentId);
        return commentService.getReplyComments(userId, commentId, sort, from, size);
    }

    @GetMapping("/events/{eventId}/comments")
    public List<CommentFullResponse> getCommentsByEventId(@PathVariable Long userId, @PathVariable Long eventId,
                                                          @RequestParam(defaultValue = "ASC") String sort,
                                                          @RequestParam(defaultValue = "0")
                                                          @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                                          @RequestParam(defaultValue = "10")
                                                          @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Получение списка комментариев к событию - {}", eventId);
        validation.checkEventId(eventId);
        return commentService.getCommentsByEventId(userId, eventId, sort, from, size);
    }
}
