package ru.practicum.ewm.client;

import jakarta.servlet.http.HttpServletRequest;
import ru.practicum.ewm.dto.StatsResponseDto;

import java.util.List;

public interface StatsClient {

    void saveHit(HttpServletRequest request);

    List<StatsResponseDto> getStats(String start, String end, List<String> uris, boolean unique);
}
