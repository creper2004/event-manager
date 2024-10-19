package ru.gorohov.eventmanager.event.permission;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import ru.gorohov.eventmanager.event.db.EventEntity;
import ru.gorohov.eventmanager.event.db.EventRepository;
import ru.gorohov.eventmanager.web.GetCurrentUserService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EventPermissionService {

    private final EventRepository eventRepository;
    private final GetCurrentUserService getCurrentUserService;

    public boolean canAuthenticatedUserModifyEvent(Long eventId) {

        var currentUser = getCurrentUserService.getCurrentUser();
        List<String> role = currentUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();
        EventEntity event = eventRepository.findById(eventId)
                .orElseThrow(() ->  new EntityNotFoundException("There is no event with id " + eventId));
        return (event.getOwnerId().equals(currentUser.getId()) || role.contains("ADMIN"));
    }
}