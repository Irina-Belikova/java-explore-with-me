package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.exception.StatsServerException;
import ru.practicum.ewm.service.StatsService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
    private final StatsService service;

    @PostMapping("/hit")
    public ResponseEntity<String> addHit(@RequestBody StatsRequestDto dto) {
        log.info("Поступил запрос на добавление данных статистики: {}", dto);
        try {
            service.addHit(dto);
            return ResponseEntity.status(HttpStatus.CREATED).body("Информация сохранена");
        } catch (Exception e) {
            throw new StatsServerException("Ошибка сохранения данных на сервере статистики.");
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<List<StatsResponseDto>> getStats(
            @RequestParam String start,
            @RequestParam String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique
    ) {
        log.info("Поступил запрос на получение статистики.");
            return ResponseEntity.ok(service.getStats(start, end, uris, unique));
    }
}
