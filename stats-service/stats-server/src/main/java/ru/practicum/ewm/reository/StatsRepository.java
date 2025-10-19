package ru.practicum.ewm.reository;

import ru.practicum.ewm.dto.ParamDto;
import ru.practicum.ewm.dto.StatsResponseDto;
import ru.practicum.ewm.model.Stats;

import java.util.List;

public interface StatsRepository {

    Stats addHit(Stats hit);

    List<StatsResponseDto> getStats(ParamDto param);
}
