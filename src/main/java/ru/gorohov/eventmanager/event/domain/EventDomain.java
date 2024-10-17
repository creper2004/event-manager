package ru.gorohov.eventmanager.event.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import ru.gorohov.eventmanager.event.EventStatus;
import ru.gorohov.eventmanager.event.entities.EventRegistrationEntity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class EventDomain {

    private Long id;

    private String name;

    private List<RegistrationDomain> registration;

    private Integer maxPlaces;

    private LocalDateTime date;

    private Integer duration;

    private Integer cost;

    private Long locationId;

    private Long ownerId;

    private EventStatus status;
}
