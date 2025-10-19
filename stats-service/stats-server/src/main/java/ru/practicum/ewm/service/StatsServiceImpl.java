package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParamDto;
import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.reository.StatsRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatsServiceImpl implements StatsService {
    private final StatsMapper mapper;
    private final StatsRepository statsRepository;

    @Override
    @Transactional
    public void addHit(StatsRequestDto dto) {
        Stats hit = mapper.mapToStats(dto);
        statsRepository.addHit(hit);
    }

    @Override
    public List<StatsResponseDto> getStats(String start, String end, List<String> uris, boolean unique) {
        ParamDto param = mapper.mapToParamDto(start, end, uris, unique);
        return statsRepository.getStats(param);
    }
}
