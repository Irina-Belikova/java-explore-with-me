package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;

import java.util.List;

public interface StatsService {

    void addHit(StatsRequestDto dto);

    List<StatsResponseDto> getStats(String start,String end, List<String> uris, boolean unique);
}
