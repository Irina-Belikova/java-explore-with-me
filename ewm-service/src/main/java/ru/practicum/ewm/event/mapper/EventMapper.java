package ru.practicum.ewm.event.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.ewm.category.mapper.CategoryMapper;
import ru.practicum.ewm.category.model.Category;
import ru.practicum.ewm.event.dto.*;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.user.mapper.UserMapper;
import ru.practicum.ewm.user.model.User;
import ru.practicum.ewm.utils.DateFormatterUtil;

import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, CategoryMapper.class, DateFormatterUtil.class},
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "category", source = "category")
    @Mapping(target = "compilationId", ignore = true)
    @Mapping(target = "eventDate", source = "eventDto.eventDate", qualifiedByName = "parseStringToDate")
    Event mapToEventFromNewEventDto(NewEventDto eventDto, Category category, User initiator);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "formatDateToString")
    @Mapping(target = "publishedOn", source = "publishedOn", qualifiedByName = "formatDateToString")
    EventFullDto mapToEventFullDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "formatDateToString")
    EventShortDto mapToEventShortDto(Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "formatDateToString")
    List<EventShortDto> mapToEventShortDtoList(List<Event> events);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "compilationId", ignore = true)
    void updateEventFromDto(UpdateEventUserRequest dto, @MappingTarget Event event);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", ignore = true)
    @Mapping(target = "initiator", ignore = true)
    @Mapping(target = "compilationId", ignore = true)
    void updateEventFromAdminDto(UpdateEventAdminRequest dto, @MappingTarget Event event);

    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "views", ignore = true)
    @Mapping(target = "createdOn", source = "createdOn", qualifiedByName = "formatDateToString")
    @Mapping(target = "eventDate", source = "eventDate", qualifiedByName = "formatDateToString")
    @Mapping(target = "publishedOn", source = "publishedOn", qualifiedByName = "formatDateToString")
    List<EventFullDto> mapToEventFullDtoList(List<Event> event);
}
