package ru.gorohov.eventmanager.event.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorohov.eventmanager.event.convector.EventConvector;
import ru.gorohov.eventmanager.event.dto.EventDto;
import ru.gorohov.eventmanager.event.requests.EventCreateRequest;
import ru.gorohov.eventmanager.event.requests.EventSearchRequest;
import ru.gorohov.eventmanager.event.requests.EventUpdateRequest;
import ru.gorohov.eventmanager.event.service.EventService;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;
    private final EventConvector eventConvector;

    @PostMapping()
    public ResponseEntity<EventDto> createEvent(@RequestBody @Valid EventCreateRequest event, Principal principal) {
        var res = eventService.createEvent(eventConvector.fromEventCreateRequestToDomain(event), principal);
        return ResponseEntity.status(201).body(eventConvector.fromDomainToDto(res));
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId) throws AccessDeniedException {
        eventService.softDeleteEvent(eventId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDto> getEvent(@PathVariable Long eventId) {
        return ResponseEntity.ok(eventConvector.fromDomainToDto(eventService.getEvent(eventId)));
    }

    @PutMapping("/{eventId}")
    public ResponseEntity<EventDto> updateEvent(@PathVariable Long eventId,
                                                @RequestBody @Valid EventUpdateRequest event) throws AccessDeniedException {
        var updated = eventService.updateEvent(eventId, eventConvector.fromEventUpdateRequestToDomain(event));
        return ResponseEntity.status(200).body(eventConvector.fromDomainToDto(updated));
    }

    @PostMapping("/search")
    public ResponseEntity<List<EventDto>> searchEventsByFilter(@RequestBody @Valid EventSearchRequest eventSearchRequest) {
        var searched = eventService.getEventsByFilter(eventSearchRequest);
        var toReturn =  searched.stream()
                .map(eventConvector::fromDomainToDto)
                .toList();
        return ResponseEntity.status(200).body(toReturn);
    }
}
