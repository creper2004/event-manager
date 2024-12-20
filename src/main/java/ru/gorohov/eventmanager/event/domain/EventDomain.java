package ru.gorohov.eventmanager.event.domain;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class EventDomain {

    private Long id;

    private String name;

    private List<EventRegistrationDomain> registration;

    private Integer maxPlaces;

    private LocalDateTime date;

    private Integer duration;

    private Integer cost;

    private Long locationId;

    private Long ownerId;

    private EventStatus status;
}
