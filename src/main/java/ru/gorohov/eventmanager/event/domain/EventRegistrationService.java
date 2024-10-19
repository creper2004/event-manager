package ru.gorohov.eventmanager.event.domain;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.eventmanager.event.convector.EventConvector;
import ru.gorohov.eventmanager.event.db.EventRegistrationEntity;
import ru.gorohov.eventmanager.web.GetCurrentUserService;
import ru.gorohov.eventmanager.event.db.EventRegistrationRepository;
import ru.gorohov.eventmanager.event.db.EventRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final GetCurrentUserService getCurrentUserService;
    private final EventConvector eventConvector;
    private final Logger log = LoggerFactory.getLogger(EventRegistrationService.class);

    @Transactional
    public void registerToEvent(Long eventId) {

        log.info("Trying registering event with: " + eventId);

        var foundedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no event with id " + eventId));

        var currentUser = getCurrentUserService.getCurrentUser();

        if (currentUser.getId().equals(foundedEvent.getOwnerId())) {
            throw new IllegalArgumentException("Owner cannot register on his event");
        }

        var registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, currentUser.getId());
        if (registration.isPresent()) {
            throw new IllegalArgumentException("You already registered to event with id " + eventId);
        }

        if (foundedEvent.getStatus().equals("STARTED")
                || foundedEvent.getStatus().equals("FINISHED")
                || foundedEvent.getStatus().equals("CANCELLED")) {
            throw new IllegalArgumentException("Cannot register to event with status " + foundedEvent.getStatus());
        }

        if (foundedEvent.getRegistration().size() == foundedEvent.getMaxPlaces()) {
            throw new IllegalArgumentException("Cannot register to event because capacity of places is full");
        }

        EventRegistrationEntity eventRegistrationEntity = new EventRegistrationEntity(null, currentUser.getId(), foundedEvent);
        var saved = eventRegistrationRepository.save(eventRegistrationEntity);
        foundedEvent.getRegistration().add(saved);
        eventRepository.save(foundedEvent);

    }

    public void cancelRegistration(Long eventId) {

        log.info("Trying cancel registration with: " + eventId);

        var currentUser = getCurrentUserService.getCurrentUser();
        var foundedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no event with id " + eventId));
        var registration = eventRegistrationRepository.findByEventIdAndUserId(eventId, currentUser.getId());
        if (registration.isEmpty()) {
            throw new IllegalArgumentException("You were not registered for the event with id " + eventId);
        }
        if (foundedEvent.getStatus().equals("STARTED")
                || foundedEvent.getStatus().equals("FINISHED")
                || foundedEvent.getStatus().equals("CANCELLED"))
        {
            throw new IllegalArgumentException("Cannot cancel register to event with status " + foundedEvent.getStatus());
        }
        eventRegistrationRepository.delete(registration.get());
        foundedEvent.getRegistration().remove(registration.get());
        eventRepository.save(foundedEvent);

    }

    public List<EventDomain> getEventsWhichUserRegistered() {
        log.info("Trying getting events which user registered");
        var currentUser = getCurrentUserService.getCurrentUser();
        var registered = eventRegistrationRepository.findAllByUserId(currentUser.getId());
        var events = registered.stream()
                .map(EventRegistrationEntity::getEvent)
                .toList();

        return events.stream()
                .map(eventConvector::fromEntityToDomain)
                .toList();
    }

}