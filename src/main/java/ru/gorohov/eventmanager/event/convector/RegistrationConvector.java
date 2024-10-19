package ru.gorohov.eventmanager.event.convector;

import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.event.domain.EventRegistrationDomain;
import ru.gorohov.eventmanager.event.db.EventRegistrationEntity;

@Component
public class RegistrationConvector {

    public EventRegistrationEntity fromDomainToEntity(final EventRegistrationDomain domain) {
        return EventRegistrationEntity.builder()
                .id(domain.getId())
                .userId(domain.getUserId())
                .event(domain.getEvent())
                .build();
    }

    public EventRegistrationDomain fromEntityToDomain(final EventRegistrationEntity entity) {
        return EventRegistrationDomain.builder()
                .id(entity.getId())
                .userId(entity.getUserId())
                .event(entity.getEvent())
                .build();
    }
}
