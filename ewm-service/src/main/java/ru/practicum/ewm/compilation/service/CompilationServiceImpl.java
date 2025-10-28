package ru.practicum.ewm.compilation.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilation.dto.CompilationDto;
import ru.practicum.ewm.compilation.dto.NewCompilationDto;
import ru.practicum.ewm.compilation.dto.UpdateCompilationRequest;
import ru.practicum.ewm.compilation.mapper.CompilationMapper;
import ru.practicum.ewm.compilation.model.Compilation;
import ru.practicum.ewm.compilation.repository.CompilationRepository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper mapper;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto dto) {
        Compilation compilation = mapper.mapToCompilationFromNewCompilationDto(dto);
        compilation = compilationRepository.save(compilation);
        List<Event> events;
        if (dto.getEvents().isEmpty()) {
            events = new ArrayList<>();
        } else {
            events = eventRepository.findAllById(dto.getEvents());
            for (Event event : events) {
                event.setCompilationId(compilation.getId());
            }
        }
        return mapper.mapToCompilationDto(compilation, events);
    }

    @Override
    @Transactional
    public void deleteCompilationBId(long id) {
        compilationRepository.deleteById(id);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(long compId, UpdateCompilationRequest dto) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборки с таким id - %d не существует.", compId)));
        mapper.updateCompilation(dto, compilation);
        List<Event> events = eventRepository.findByCompilationId(compId);

        if (dto.getEvents().isEmpty()) {
            return mapper.mapToCompilationDto(compilation, events);
        }

        events.forEach(event -> event.setCompilationId(null));
        List<Event> newEvents = eventRepository.findAllById(dto.getEvents());
        newEvents.forEach(event -> event.setCompilationId(compId));
        return mapper.mapToCompilationDto(compilation, newEvents);
    }

    @Override
    public CompilationDto getCompilationById(long compId) {
        Compilation compilation = compilationRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format("Подборки с таким id - %d не существует.", compId)));
        List<Event> events = eventRepository.findByCompilationId(compId);
        return mapper.mapToCompilationDto(compilation, events);
    }

    @Override
    public List<CompilationDto> getCompilations(boolean pinned, int from, int size) {
        Pageable page = PageRequest.of(from, size);
        List<Compilation> compilations = compilationRepository.findByPinned(pinned, page).getContent();
        List<Long> compIds = compilations.stream().map(Compilation::getId).toList();
        Map<Long, List<Event>> events = eventRepository.getMapOfEventsByCompId(compIds);
        return compilations.stream()
                .map(compilation -> mapper.mapToCompilationDto(
                        compilation,
                        events.get(compilation.getId())
                ))
                .collect(Collectors.toList());
    }
}
