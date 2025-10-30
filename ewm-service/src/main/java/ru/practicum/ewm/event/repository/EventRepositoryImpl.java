package ru.practicum.ewm.event.repository;

import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import ru.practicum.ewm.event.model.Event;
import ru.practicum.ewm.event.model.QEvent;
import ru.practicum.ewm.event.model.StatusState;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EventRepositoryImpl implements EventRepositoryCustom {

    private final JPAQueryFactory queryFactory;
    private final QEvent event = QEvent.event;


    @Override
    public List<Event> findEventsByParams(List<Long> users, List<StatusState> states,
                                          List<Long> categories, LocalDateTime rangeStart,
                                          LocalDateTime rangeEnd, int from, int size) {

        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .offset(from)
                .limit(size);

        if (users != null && !users.isEmpty()) {
            query.where(event.initiator.id.in(users));
        }

        if (states != null && !states.isEmpty()) {
            query.where(event.state.in(states));
        }

        if (categories != null && !categories.isEmpty()) {
            query.where(event.category.id.in(categories));
        }

        if (rangeStart != null) {
            query.where(event.eventDate.goe(rangeStart));
        }

        if (rangeEnd != null) {
            query.where(event.eventDate.loe(rangeEnd));
        }

        query.orderBy(event.createdOn.desc());
        return query.fetch();
    }

    @Override
    public List<Event> getEventsForPublic(StatusState state, String text, List<Long> categories, Boolean paid,
                                          LocalDateTime rangeStart, LocalDateTime rangeEnd, int from, int size) {
        JPAQuery<Event> query = queryFactory
                .selectFrom(event)
                .offset(from)
                .limit(size);

        query.where(event.state.eq(StatusState.PUBLISHED));

        if (text != null && !text.trim().isEmpty()) {
            String searchText = "%" + text.toLowerCase() + "%";
            query.where(event.annotation.toLowerCase().like(searchText)
                    .or(event.description.toLowerCase().like(searchText)));
        }

        if (categories != null && !categories.isEmpty()) {
            query.where(event.category.id.in(categories));
        }

        if (paid != null) {
            query.where(event.paid.eq(paid));
        }

        LocalDateTime actualRangeStart = rangeStart != null ? rangeStart : LocalDateTime.now();
        query.where(event.eventDate.goe(actualRangeStart));

        if (rangeEnd != null) {
            query.where(event.eventDate.loe(rangeEnd));
        }

        query.orderBy(event.eventDate.desc());
        return query.fetch();
    }
}
