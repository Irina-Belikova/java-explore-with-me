package ru.practicum.ewm.compilation.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.service.CompilationService;
import ru.practicum.ewm.utils.ValidationUtil;

@RestController
@RequestMapping("/admin/compilations")
@RequiredArgsConstructor
@Validated
@Slf4j
public class AdminCompilationController {
    private final CompilationService compilationService;
    private final ValidationUtil validation;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public CompilationDto addCompilation(@Valid @RequestBody NewCompilationDto dto) {
        log.info("Запрос на добавление новой подборки: {}", dto);
        validation.validationCompilationTitle(dto.getTitle());
        return compilationService.addCompilation(dto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void deleteCompilationById(@PathVariable Long compId) {
        log.info("Запрос на удаление подборки - {}.", compId);
        validation.checkCompilationId(compId);
        compilationService.deleteCompilationBId(compId);
    }

    @PatchMapping("/{compId}")
    @ResponseStatus(code = HttpStatus.OK)
    public CompilationDto updateCompilation(@PathVariable Long compId, @Valid @RequestBody UpdateCompilationRequest dto) {
        log.info("Запрос на изменение подборки - {} с dto: {}", compId, dto);
        validation.checkCompilationId(compId);
        return compilationService.updateCompilation(compId, dto);
    }
}
