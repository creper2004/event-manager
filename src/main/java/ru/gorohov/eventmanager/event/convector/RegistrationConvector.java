package ru.gorohov.eventmanager.event.convector;

import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.event.domain.RegistrationDomain;
import ru.gorohov.eventmanager.event.entities.EventRegistrationEntity;

@Component
public class RegistrationConvector {

    public EventRegistrationEntity fromDomainToEntity(final RegistrationDomain domain) {
        return EventRegistrationEntity.builder()
                .id(domain.getId())
                .event(domain.getEvent())
                .build();
    }

    public RegistrationDomain fromEntityToDomain(final EventRegistrationEntity entity) {
        return RegistrationDomain.builder()
                .id(entity.getId())
                .event(entity.getEvent())
                .build();
    }
}
