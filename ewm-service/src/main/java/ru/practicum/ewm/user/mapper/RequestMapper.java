package ru.practicum.ewm.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.user.dto.ParticipationRequestDto;
import ru.practicum.ewm.user.model.Request;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.util.List;

@Mapper(componentModel = "spring", uses = DateFormatterUtil.class,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface RequestMapper {

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    ParticipationRequestDto mapToRequestDto(Request request);

    @Mapping(target = "event", source = "event.id")
    @Mapping(target = "requester", source = "requester.id")
    List<ParticipationRequestDto> mapToRequestDtoList(List<Request> requests);
}
