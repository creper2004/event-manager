package ru.gorohov.eventmanager.event.api;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorohov.eventmanager.event.convector.EventConvector;
import ru.gorohov.eventmanager.event.domain.EventRegistrationService;

import java.util.List;

@RestController
@RequestMapping("/events/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;
    private final EventConvector eventConvector;

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerToEvent(@PathVariable Long eventId) {
        eventRegistrationService.registerToEvent(eventId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/cancel/{eventId}")
    public ResponseEntity<Void> cancelRegistration(@PathVariable Long eventId) {
        eventRegistrationService.cancelRegistration(eventId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/my")
    public ResponseEntity<List<EventDto>> getEventsForCurrentUser() {
        var founded = eventRegistrationService.getEventsWhichUserRegistered();
        var toReturn = founded.stream().map(eventConvector::fromDomainToDto).toList();
        return ResponseEntity.ok().body(toReturn);
    }
}
