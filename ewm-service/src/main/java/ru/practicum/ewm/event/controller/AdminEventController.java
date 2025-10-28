package ru.practicum.ewm.event.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.event.dto.EventFullDto;
import ru.practicum.ewm.event.dto.EventRequestParam;
import ru.practicum.ewm.event.dto.UpdateEventAdminRequest;
import ru.practicum.ewm.event.service.EventService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/admin/events")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminEventController {
    private final EventService eventService;
    private final ValidationUtil validation;

    @PatchMapping("/{eventId}")
    @ResponseStatus(code = HttpStatus.OK)
    public EventFullDto updateEventByAdmin(@PathVariable Long eventId, @Valid @RequestBody UpdateEventAdminRequest dto) {
        log.info("Запрос от администратора на изменение события - {}, {}", eventId, dto);
        validation.validationUpdateEventByAdmin(eventId, dto);
        return eventService.updateEventByAdmin(eventId, dto);
    }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<EventFullDto> getEventsForAdmin(@RequestParam(required = false) List<Long> users,
                                                @RequestParam(required = false) List<String> states,
                                                @RequestParam(required = false) List<Long> categories,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                                @RequestParam(required = false)
                                                @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                                @RequestParam(defaultValue = "0")
                                                @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                                @RequestParam(defaultValue = "10")
                                                @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Запрос администратора на получение событий: users={}, states={}, categories={}, rangeStart={}, rangeEnd={}, from={}, size={}",
                users, states, categories, rangeStart, rangeEnd, from, size);
        EventRequestParam param = EventRequestParam.builder()
                .users(users)
                .states(states)
                .categories(categories)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .from(from)
                .size(size)
                .build();
        return eventService.getEventsForAdmin(param);
    }
}
