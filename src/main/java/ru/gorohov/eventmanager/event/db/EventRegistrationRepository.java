package ru.gorohov.eventmanager.event.db;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    Optional<EventRegistrationEntity> findByEventIdAndUserId(Long event_id, Long userId);
    List<EventRegistrationEntity> findAllByUserId(Long userId);
}
