package ru.practicum.ewm.user.request.storage;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.user.request.model.Request;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAllByRequesterId(Long id);

    List<Request> findAllByEventIdAndRequesterId(Long eventId, Long userId);

    Page<Request> findAllByEventId(Long eventId, Pageable pageable);

    Optional<Request> findByIdAndRequesterId(Long requesterId, Long id);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    int countAllByEventId(Long eventId);
}
