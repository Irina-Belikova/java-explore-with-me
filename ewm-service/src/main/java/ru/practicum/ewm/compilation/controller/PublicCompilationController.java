package ru.practicum.ewm.compilation.controller;

import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.utils.ValidationUtil;

import java.util.List;

@RestController
@RequestMapping("/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class PublicCompilationController {
    private final CompilationService compilationService;
    private final ValidationUtil validation;

    @GetMapping("/{compId}")
    public CompilationDto getCompilationById(@PathVariable Long compId) {
        log.info("Запрос на получение подборки событий по его id -{}.", compId);
        validation.checkCompilationId(compId);
        return compilationService.getCompilationById(compId);
    }

    @GetMapping
    public List<CompilationDto> getCompilations(@RequestParam(required = false) Boolean pinned,
                                                @RequestParam(defaultValue = "0")
                                                @Min(value = 0, message = "Параметр 'from' должен быть не меньше 0") int from,
                                                @RequestParam(defaultValue = "10")
                                                @Min(value = 1, message = "Параметр 'size' должен быть не меньше 1") int size) {
        log.info("Запрос на подборку событий с pinned - {}, from - {}, size - {}", pinned, from, size);
        return compilationService.getCompilations(pinned, from, size);
    }
}
