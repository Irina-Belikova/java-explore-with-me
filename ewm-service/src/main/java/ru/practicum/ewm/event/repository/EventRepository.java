package ru.practicum.ewm.event.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.event.model.Event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface EventRepository extends JpaRepository<Event, Long>, EventRepositoryCustom {

    boolean existsByCategoryId(Long categoryId);

    Page<Event> findByInitiatorId(Long initiatorId, Pageable pageable);

    @Query("SELECT e.initiator.id FROM Event e WHERE e.id = :eventId")
    Long getInitiatorId(long eventId);

    @Query("SELECT e.participantLimit FROM Event e WHERE e.id = :eventId")
    Integer getParticipantLimit(long eventId);

    List<Event> findByCompilationId(Long compilationId);

    @Query("SELECT e.compilationId, e FROM Event e WHERE compilationId IN :compIds")
    List<Object[]> getEventsByCompId(List<Long> compIds);

    default Map<Long, List<Event>> getMapOfEventsByCompId(List<Long> compIds) {
        List<Object[]> objects = getEventsByCompId(compIds);
        Map<Long, List<Event>> eventMaps = new HashMap<>();
        for (Object[] object : objects) {
            long id = (Long) object[0];
            Event event = (Event) object[1];
            if (eventMaps.containsKey(id)) {
                eventMaps.get(id).add(event);
            } else {
                List<Event> list = new ArrayList<>();
                list.add(event);
                eventMaps.put(id, list);
            }
        }
        return eventMaps;
    }
}
