package ru.gorohov.eventmanager.event.convector;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.event.domain.EventStatus;
import ru.gorohov.eventmanager.event.domain.EventDomain;
import ru.gorohov.eventmanager.event.api.EventDto;
import ru.gorohov.eventmanager.event.db.EventEntity;
import ru.gorohov.eventmanager.event.request.EventCreateRequest;
import ru.gorohov.eventmanager.event.request.EventUpdateRequest;

import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class EventConvector {

    private final RegistrationConvector registrationConvector;

    public EventDomain fromEventCreateRequestToDomain(final EventCreateRequest eventCreateRequest) {
        return EventDomain.builder()
                .id(null) //сделает БД
                .name(eventCreateRequest.getName())
                .registration(new ArrayList<>())
                .maxPlaces(eventCreateRequest.getMaxPlaces())
                .date(eventCreateRequest.getDate())
                .duration(eventCreateRequest.getDuration())
                .cost(eventCreateRequest.getCost())
                .locationId(eventCreateRequest.getLocationId())
                .ownerId(null) // в кнотроллере заинжектим
                .status(EventStatus.WAIT_START)
                .build();
    }

    public EventDomain fromEventUpdateRequestToDomain(final EventUpdateRequest eventUpdateRequest) {
        return EventDomain.builder()
                .id(null)
                .name(eventUpdateRequest.getName())
                .registration(null)
                .maxPlaces(eventUpdateRequest.getMaxPlaces())
                .date(eventUpdateRequest.getDate())
                .duration(eventUpdateRequest.getDuration())
                .cost(eventUpdateRequest.getCost())
                .locationId(eventUpdateRequest.getLocationId())
                .ownerId(null)
                .status(null)
                .build();
    }

    public EventEntity fromDomainToEntity(final EventDomain eventDomain) {
        return EventEntity.builder()
                .id(eventDomain.getId())
                .name(eventDomain.getName())
                .registration(eventDomain.getRegistration().stream()
                        .map(registrationConvector::fromDomainToEntity)
                        .toList())
                .maxPlaces(eventDomain.getMaxPlaces())
                .date(eventDomain.getDate())
                .duration(eventDomain.getDuration())
                .cost(eventDomain.getCost())
                .locationId(eventDomain.getLocationId())
                .ownerId(eventDomain.getOwnerId())
                .status(String.valueOf(eventDomain.getStatus()))
                .build();
    }

    public EventDomain fromEntityToDomain(final EventEntity eventEntity) {
        return EventDomain.builder()
                .id(eventEntity.getId())
                .name(eventEntity.getName())
                .registration(eventEntity.getRegistration().stream()
                        .map(registrationConvector::fromEntityToDomain)
                        .toList())
                .maxPlaces(eventEntity.getMaxPlaces())
                .date(eventEntity.getDate())
                .duration(eventEntity.getDuration())
                .cost(eventEntity.getCost())
                .locationId(eventEntity.getLocationId())
                .ownerId(eventEntity.getOwnerId())
                .status(EventStatus.valueOf(eventEntity.getStatus()))
                .build();
    }

    public EventDto fromDomainToDto(final EventDomain eventDomain) {
        return EventDto.builder()
                .id(eventDomain.getId())
                .name(eventDomain.getName())
                .ownerId(eventDomain.getOwnerId())
                .maxPlaces(eventDomain.getMaxPlaces())
                .occupiedPlaces(eventDomain.getRegistration().size())
                .date(eventDomain.getDate())
                .cost(eventDomain.getCost())
                .duration(eventDomain.getDuration())
                .locationId(eventDomain.getLocationId())
                .status(String.valueOf(eventDomain.getStatus()))
                .build();
    }

}
