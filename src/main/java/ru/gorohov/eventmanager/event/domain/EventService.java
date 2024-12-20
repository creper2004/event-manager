package ru.gorohov.eventmanager.event.domain;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.HistoryOfFields;
import ru.gorohov.InfoOfEditedEvent;
import ru.gorohov.eventmanager.event.convector.EventConvector;
import ru.gorohov.eventmanager.event.db.EventEntity;
import ru.gorohov.eventmanager.event.db.EventRegistrationEntity;
import ru.gorohov.eventmanager.event.permission.EventPermissionService;
import ru.gorohov.eventmanager.kafka.KafkaSender;
import ru.gorohov.eventmanager.web.GetCurrentUserService;
import ru.gorohov.eventmanager.event.db.EventRepository;
import ru.gorohov.eventmanager.event.request.EventSearchRequest;
import ru.gorohov.eventmanager.location.db.LocationsRepository;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EventService {

    private final EventRepository eventRepository;
    private final EventConvector eventConvector;
    private final EventPermissionService eventPermissionService;
    private final LocationsRepository locationsRepository;
    private final GetCurrentUserService getCurrentUserService;
    private final KafkaSender kafkaSender;
    private final Logger log = LoggerFactory.getLogger(EventService.class);

    @Transactional
    public EventDomain createEvent(EventDomain eventDomain) {

        var currentUser = getCurrentUserService.getCurrentUser();
        log.info("Creating event {} for user {} ", eventDomain.getName(), currentUser.getUsername());
        var foundLocation = locationsRepository.findById(eventDomain.getLocationId())
                .orElseThrow(() -> new EntityNotFoundException("Location with id " + eventDomain.getLocationId() + " not found"));
        if (eventDomain.getMaxPlaces() > foundLocation.getCapacity()) {
            throw new IllegalArgumentException("Maximum number of places exceeded. Max allowed is " + foundLocation.getCapacity() + " but found " + eventDomain.getMaxPlaces());
        }
        if (!checkIntersection(eventDomain.getLocationId(), eventDomain.getDate(),
                eventDomain.getDate().plusMinutes(eventDomain.getDuration()), null))
        {
            throw new IllegalArgumentException("Location with id " + eventDomain.getLocationId() + " is busy for your time");
        }

        eventDomain.setOwnerId(currentUser.getId());
        var saved = eventRepository.save(eventConvector.fromDomainToEntity(eventDomain));

        return eventConvector.fromEntityToDomain(saved);
    }

    @Transactional
    public void softDeleteEvent(Long eventId) throws AccessDeniedException {
        log.info("Deleting event {}", eventId);
        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));

        if (eventPermissionService.canAuthenticatedUserModifyEvent(eventId))  {

            if (found.getStatus().equals("CANCELLED")) {
                return;
            }
            if (LocalDateTime.now().isBefore(found.getDate())) {
                found.setStatus("CANCELLED");
                HistoryOfFields<String> status = HistoryOfFields.<String>builder()
                        .oldField(found.getStatus())
                        .newField("CANCELLED")
                        .build();
                List<Long> usedIds = found.getRegistration()
                        .stream().map(EventRegistrationEntity::getUserId)
                        .toList();
                var message = InfoOfEditedEvent.builder()
                        .eventId(eventId)
                        .ownerId(found.getOwnerId())
                        .status(status)
                        .registeredUsersId(usedIds)
                        .build();
                eventRepository.save(found);
                kafkaSender.sendMessage(message, "changed-event-topic");
            }
            else {
                throw new RuntimeException("Cannot cancel event which status: " + found.getStatus());
            }

        }
        else {
            throw new AccessDeniedException("Not enough rights. You must be the event creator or administrator");
        }
    }

    public EventDomain getEvent(Long eventId) {
        log.info("Getting event {}", eventId);
        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        return eventConvector.fromEntityToDomain(found);
    }

    @Transactional
    public EventDomain updateEvent(Long eventId, EventDomain eventDomain) throws AccessDeniedException {
        log.info("Updating event {}", eventId);
        var found = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("Event with id " + eventId + " not found"));
        HashMap<String, Object> oldFields = getOldFields(found);
        log.info("Old fields: {}", oldFields);

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

            if (!checkIntersection(found.getLocationId(), found.getDate(),
                    found.getDate().plusMinutes(found.getDuration()), eventId))
            {
                throw new IllegalArgumentException("Location with id " + eventDomain.getLocationId() + " is busy for your time");
            }

            List<Long> usedIds = found.getRegistration()
                    .stream().map(EventRegistrationEntity::getUserId)
                    .toList();
            InfoOfEditedEvent info = InfoOfEditedEvent.builder()
                    .eventId(eventId)
                    .ownerId(found.getOwnerId())
                    .name(new HistoryOfFields<String>((String) oldFields.get("name"), found.getName()))
                    .maxPlaces(new HistoryOfFields<Integer>((Integer) oldFields.get("maxPlaces"), found.getMaxPlaces()))
                    .date(new HistoryOfFields<LocalDateTime>((LocalDateTime) oldFields.get("date"), found.getDate()))
                    .cost(new HistoryOfFields<Integer>((Integer) oldFields.get("cost"), found.getCost()))
                    .duration(new HistoryOfFields<Integer>((Integer) oldFields.get("duration"), found.getDuration()))
                    .locationId(new HistoryOfFields<Long>((Long) oldFields.get("locationId"), found.getLocationId()))
                    .status(new HistoryOfFields<String>((String) oldFields.get("status"),  found.getStatus()))
                    .registeredUsersId(usedIds)
                    .build();


            eventRepository.save(found);
            kafkaSender.sendMessage(info,"changed-event-topic");
            return eventConvector.fromEntityToDomain(found);
        }
        else {
            throw new AccessDeniedException("Not enough rights. You must be the event creator or administrator");
        }
    }

    public List<EventDomain> getEventsByFilter(EventSearchRequest eventSearchRequest) {

        log.info("Getting events by filter");

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

    public List<EventDomain> getEventsWhichCurrentUserIsOwner() {
        log.info("Getting events which current user is owner");
        var currentUser = getCurrentUserService.getCurrentUser();
        var foundedEvents = eventRepository.findEventByOwnerId(currentUser.getId());
        return foundedEvents.stream()
                .map(eventConvector::fromEntityToDomain)
                .toList();
    }


    private boolean checkIntersection(Long locationId, LocalDateTime start, LocalDateTime end, Long eventId) {
        if (start.isAfter(end)) {
            throw new IllegalArgumentException("Start time is after end time");
        }
        var foundedEvents = eventRepository.findEventsByLocationIdAndInterval(locationId, start, end);

        if (eventId != null && foundedEvents.size() == 1 && foundedEvents.get(0).getId().equals(eventId)) {
            return true;
        }
        return foundedEvents.isEmpty();
    }

    private HashMap<String, Object> getOldFields(EventEntity eventEntity) {
        HashMap<String, Object> oldFields = new HashMap<>();
        oldFields.put("name", eventEntity.getName());
        oldFields.put("maxPlaces", eventEntity.getMaxPlaces());
        oldFields.put("date", eventEntity.getDate());
        oldFields.put("cost", eventEntity.getCost());
        oldFields.put("duration", eventEntity.getDuration());
        oldFields.put("locationId", eventEntity.getLocationId());
        oldFields.put("status", eventEntity.getStatus());
        return oldFields;
    }
}