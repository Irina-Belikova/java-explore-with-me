package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.ParamDto;
import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.exception.BadParameterException;
import ru.practicum.ewm.mapper.StatsMapper;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.reository.StatsRepository;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.time.LocalDateTime;
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
            LocalDateTime startDate = DateFormatterUtil.parseDate(start);
            LocalDateTime endDate = DateFormatterUtil.parseDate(end);
            if (endDate.isBefore(startDate)) {
                throw new BadParameterException("Дата окончания выборки должна быть после даты начала.");
            }
        ParamDto param = mapper.mapToParamDto(start, end, uris, unique);
        return statsRepository.getStats(param);
    }
}
