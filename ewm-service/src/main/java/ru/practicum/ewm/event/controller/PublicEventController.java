package ru.practicum.ewm.event.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.client.StatsClient;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventShortDto;
import ru.practicum.ewm.event.dto.GetEventsRequest;
import ru.practicum.ewm.event.model.StatusState;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicEventController {
    private final EventService eventService;
    private final ValidationUtil validation;
    private final StatsClient statsClient;

    @GetMapping("/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto getEventById(@PathVariable Long id, HttpServletRequest request) {
        log.info("Публичный запрос на получение события по id - {}", id);
        validation.validationGetEventById(id);
        statsClient.saveHit(request);
        return eventService.getEventById(id);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EventShortDto> getEventsByParam(@RequestParam(required = false) String text,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false) boolean paid,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "false") boolean onlyAvailable,
                                                @RequestParam(required = false, defaultValue = "EVENT_DATE") String sort,
                                                @RequestParam(required = false, defaultValue = "0")
                                                @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                                @RequestParam(required = false, defaultValue = "10")
                                                @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size,
                                                HttpServletRequest request) {
        log.info("Публичный запрос на получение списка событий.");
        GetEventsRequest param = GetEventsRequest.builder()
                .state(StatusState.PUBLISHED)
                .text(text)
                .categories(categories)
                .paid(paid)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .onlyAvailable(onlyAvailable)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        List<EventShortDto> dto = eventService.getEventsByParam(param);
        statsClient.saveHit(request);
        return dto;
    }
}
