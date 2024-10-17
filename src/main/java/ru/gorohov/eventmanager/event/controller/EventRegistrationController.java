package ru.gorohov.eventmanager.event.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.gorohov.eventmanager.event.service.EventRegistrationService;

@RestController
@RequestMapping("/events/registrations")
@RequiredArgsConstructor
public class EventRegistrationController {

    private final EventRegistrationService eventRegistrationService;

    @PostMapping("/{eventId}")
    public ResponseEntity<Void> registerToEvent(@PathVariable Long eventId) {
        eventRegistrationService.registerToEvent(eventId);
        return ResponseEntity.ok().build();
    }
}
