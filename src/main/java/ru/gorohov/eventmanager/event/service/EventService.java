package ru.gorohov.eventmanager.event.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.eventmanager.event.EventStatus;
import ru.gorohov.eventmanager.event.convector.EventConvector;
import ru.gorohov.eventmanager.event.domain.EventDomain;
import ru.gorohov.eventmanager.event.permission.EventPermissionService;
import ru.gorohov.eventmanager.event.repository.EventRepository;
import ru.gorohov.eventmanager.event.requests.EventSearchRequest;
import ru.gorohov.eventmanager.location.LocationsRepository;
import ru.gorohov.eventmanager.location.LocationsService;
import ru.gorohov.eventmanager.secured.repository.UserRepository;
import ru.gorohov.eventmanager.secured.service.UserService;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final UserService userService;
    private final EventConvector eventConvector;
    private final EventPermissionService eventPermissionService;
    private final LocationsRepository locationsRepository;
    private final Logger log = LoggerFactory.getLogger(EventService.class);

    @Transactional
    public EventDomain createEvent(EventDomain eventDomain, Principal principal) {

        var foundLocation = locationsRepository.findById(eventDomain.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + eventDomain.getLocationId() + " not found"));
        if (eventDomain.getMaxPlaces() > foundLocation.getCapacity()) {
            throw new IllegalArgumentException("Maximum number of places exceeded");
        }

        var name = principal.getName();
        var userId = userService.getUserByLogin(name).getId();
        eventDomain.setOwnerId(userId);
        var saved = eventRepository.save(eventConvector.fromDomainToEntity(eventDomain));

        return eventConvector.fromEntityToDomain(saved);
    }

    @Transactional
    public void softDeleteEvent(Long eventId) throws AccessDeniedException {

        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (eventPermissionService.canAuthenticatedUserModifyEvent(eventId))  {

            if (LocalDateTime.now().isBefore(found.getDate())) {
                found.setStatus("CANCELLED");
                eventRepository.save(found);
            }
            else {
                throw new RuntimeException("Cannot delete event which already starts");
            }

        }
        else {
            throw new AccessDeniedException("Not enough rights. You must be the event creator or administrator");
        }
    }

    public EventDomain getEvent(Long eventId) {
        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        return eventConvector.fromEntityToDomain(found);
    }

    @Transactional
    public EventDomain updateEvent(Long eventId, EventDomain eventDomain) throws AccessDeniedException {
        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (eventPermissionService.canAuthenticatedUserModifyEvent(eventId))  {

            if (!found.getStatus().equals("WAIT_START")) {
                throw new IllegalArgumentException("Cannot modify event in status: %s"
                        .formatted(found.getStatus()));
            }
            var locationId = Optional.ofNullable(eventDomain.getLocationId()).orElse(found.getLocationId());
            var location = locationsRepository.findById(locationId)
                    .orElseThrow(() -> new EntityNotFoundException("Location with id " + locationId + " not found"));
            var maxPlaces = Optional.ofNullable(eventDomain.getMaxPlaces()).orElse(found.getMaxPlaces());
            if (maxPlaces > location.getCapacity()) {
                throw new IllegalArgumentException(
                        "Capacity of location less than maxPlaces: capacity=%s, maxPlaces=%s"
                                .formatted(location.getCapacity(), maxPlaces)
                );
            }
            if (maxPlaces < found.getRegistration().size()) {
                throw new IllegalArgumentException(
                        "Registration count is more than maxPlaces: regCount=%s, maxPlaces=%s"
                                .formatted(found.getRegistration().size(), eventDomain.getMaxPlaces()));
            }
            Optional.ofNullable(eventDomain.getName()).ifPresent(found::setName);
            Optional.ofNullable(eventDomain.getMaxPlaces()).ifPresent(found::setMaxPlaces);
            Optional.ofNullable(eventDomain.getDate()).ifPresent(found::setDate);
            Optional.ofNullable(eventDomain.getDuration()).ifPresent(found::setDuration);
            Optional.ofNullable(eventDomain.getCost()).ifPresent(found::setCost);
            Optional.ofNullable(eventDomain.getLocationId()).ifPresent(found::setLocationId);
            eventRepository.save(found);
            return eventConvector.fromEntityToDomain(found);
        }
        else {
            throw new AccessDeniedException("Not enough rights. You must be the event creator or administrator");
        }
    }

    public List<EventDomain> getEventsByFilter(EventSearchRequest eventSearchRequest) {

        var found = eventRepository.findEvents(eventSearchRequest.getName(),
                eventSearchRequest.getPlacesMin(),
                eventSearchRequest.getPlacesMax(),
                eventSearchRequest.getDateStartAfter(),
                eventSearchRequest.getDateStartBefore(),
                eventSearchRequest.getCostMin(),
                eventSearchRequest.getCostMax(),
                eventSearchRequest.getDurationMin(),
                eventSearchRequest.getDurationMax(),
                eventSearchRequest.getLocationId(),
                eventSearchRequest.getEventStatus());

        return found.stream()
                .map(eventConvector::fromEntityToDomain)
                .toList();
    }

}
