package ru.practicum.ewm.client;

import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.dto.StatsResponseDto;

import java.util.List;

public interface StatsClient {

    void saveHit(StatsRequestDto dto);

    List<StatsResponseDto> getStats(String start, String end, List<String> uris, boolean unique);
}
