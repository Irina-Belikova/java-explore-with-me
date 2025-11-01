package ru.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.ewm.dto.ParamDto;
import ru.practicum.ewm.dto.StatsRequestDto;
import ru.practicum.ewm.model.Stats;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.util.List;


@Mapper(componentModel = "spring", uses = DateFormatterUtil.class)
public interface StatsMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "timestamp", source = "timestamp", qualifiedByName = "parseStringToDate")
    Stats mapToStats(StatsRequestDto dto);

    default ParamDto mapToParamDto(String start, String end, List<String> uris, boolean unique) {
        ParamDto paramDto = ParamDto.builder()
                .start(DateFormatterUtil.parseDate(start))
                .end(DateFormatterUtil.parseDate(end))
                .unique(unique)
                .build();
        if (uris != null) {
            paramDto.setUris(uris);
        }
        return paramDto;
    }
}
