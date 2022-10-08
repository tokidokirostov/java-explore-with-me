package ru.practicum.ewm.user.event.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.ewm.user.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface EventStorage extends JpaRepository<Event, Long> {

    @Query("SELECT e from Event as e " +
            "where ((:users) is null or e.initiator.id in :users)" +
            "and ((:states) is null or e.state in :states)" +
            "and ((:categories) is null or e.category.id in :categories)" +
            "and (:rangeStart <= e.createdOn) and (:rangeEnd >= e.createdOn)")
    Page<Event> searchEventByAdmin(List<Long> users, List<String> states, List<Long> categories,
                                   LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);

    Optional<Event> findByCategoryId(Long id);

    Page<Event> findAllByInitiatorId(Long id, Pageable page);

    Optional<Event> findByIdAndInitiatorId(Long eventId, Long userId);

    @Query(value = "SELECT e FROM Event as e " +
            "WHERE ((:text) is null or ((e.annotation like %:text%) or (e.description like %:text%))) " +
            "and (e.state = 'PUBLISHED') " +
            "and ((:categories) is null or e.category.id in :categories) " +
            "and (e.paid = :paid or :paid is null)"  +
            "and ((:onlyAvailable) is null or e.participantLimit > e.confirmedRequests) " +
            "and (:rangeStart <= e.createdOn) and (:rangeEnd >= e.createdOn)")
    Page<Event> searchEvents(String text, List<Long> categories, Boolean paid, Boolean onlyAvailable,
                           LocalDateTime rangeStart, LocalDateTime rangeEnd, Pageable page);
}
