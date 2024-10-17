package ru.gorohov.eventmanager.event.service;


import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gorohov.eventmanager.event.entities.EventRegistrationEntity;
import ru.gorohov.eventmanager.event.repository.EventRegistrationRepository;
import ru.gorohov.eventmanager.event.repository.EventRepository;
import ru.gorohov.eventmanager.secured.service.UserService;
import ru.gorohov.eventmanager.secured.user.UserDomain;

@Service
@RequiredArgsConstructor
public class EventRegistrationService {

    private final EventRegistrationRepository eventRegistrationRepository;
    private final EventRepository eventRepository;
    private final UserService userService;

    @Transactional
    public void registerToEvent(Long eventId) {

        var foundedEvent = eventRepository.findById(eventId)
                .orElseThrow(() -> new EntityNotFoundException("There is no event with id " + eventId));

        var currentUser = getCurrentUser();

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

    private UserDomain getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("You do not have permission to do this.");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof User currentUser) {
            username = currentUser.getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;
        } else {
            throw new SecurityException("Unknown principal type");
        }
        return userService.getUserByLogin(username);

    }

}
