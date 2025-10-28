package ru.practicum.ewm.compilation.service;

import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {

    CompilationDto addCompilation(NewCompilationDto dto);

    void deleteCompilationBId(long id);

    CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto);

    CompilationDto getCompilationById(long compId);

    List<CompilationDto> getCompilations(boolean pinned, int from, int size);
}
