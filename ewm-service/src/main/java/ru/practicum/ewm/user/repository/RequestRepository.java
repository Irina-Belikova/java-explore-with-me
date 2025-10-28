package ru.practicum.ewm.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.model.Request;
import ru.practicum.ewm.user.model.RequestStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface RequestRepository extends JpaRepository<Request, Long> {

    boolean existsByRequesterId(long requesterId);

    @Query("SELECT COUNT(*) FROM Request r WHERE r.event.id = :eventId AND r.status = RequestStatus.CONFIRMED")
    Integer getCountRequestsByEventId(long eventId);

    List<Request> findByRequesterId(long requesterId);

    List<Request> findByEventId(long eventId);

    @Query("SELECT r.status FROM Request r WHERE r.id IN :requestIds")
    List<RequestStatus> getRequestStatusListForUpdate(List<Long> requestIds);

    List<Request> findByIdIn(List<Long> requestIds);

    @Query("SELECT r.event.id, COUNT(*) FROM Request r WHERE r.event.id IN :eventIds AND r.status = RequestStatus.CONFIRMED")
    List<Object[]> getConfirmedRequests(List<Long> eventIds);

    default Map<Long, Integer> getMapOfCountRequests(List<Long> eventIds) {
        List<Object[]> objects = getConfirmedRequests(eventIds);
        Map<Long, Integer> confirmedMaps = new HashMap<>();
        for (Object[] object : objects) {
            confirmedMaps.put((Long) object[0], (Integer) object[1]);
        }
        return confirmedMaps;
    }
}
