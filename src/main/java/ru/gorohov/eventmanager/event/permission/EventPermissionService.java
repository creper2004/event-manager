package ru.gorohov.eventmanager.event.permission;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import ru.gorohov.eventmanager.event.entities.EventEntity;
import ru.gorohov.eventmanager.event.repository.EventRepository;
import ru.gorohov.eventmanager.secured.service.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPermissionService {

    private final EventRepository eventRepository;
    private final UserService userService;

    public boolean canAuthenticatedUserModifyEvent(Long eventId) {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new SecurityException("You do not have permission to do this.");
        }

        Object principal = authentication.getPrincipal();
        String username;

        if (principal instanceof User currentUser) {
            username = currentUser.getUsername();
        } else if (principal instanceof String) {
            username = (String) principal;  // Если principal — это строка (логин)
        } else {
            throw new SecurityException("Unknown principal type");
        }

        List<String> role = authentication.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        var userId = userService.getUserByLogin(username).getId();
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() ->  new EntityNotFoundException("There is no event with id " + eventId));
        return (event.getOwnerId().equals(userId) || role.contains("ADMIN"));

    }
}
