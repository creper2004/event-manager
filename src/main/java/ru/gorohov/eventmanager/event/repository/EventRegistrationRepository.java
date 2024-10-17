package ru.gorohov.eventmanager.event.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.gorohov.eventmanager.event.entities.EventRegistrationEntity;

import java.util.Optional;

public interface EventRegistrationRepository extends JpaRepository<EventRegistrationEntity, Long> {

    Optional<EventRegistrationEntity> findByEventIdAndUserId(Long event_id, Long userId);
}
