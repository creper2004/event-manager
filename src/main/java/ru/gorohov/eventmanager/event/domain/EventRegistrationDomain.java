package ru.gorohov.eventmanager.event.domain;

import lombok.Builder;
import lombok.Data;
import ru.gorohov.eventmanager.event.db.EventEntity;

@Data
@Builder
public class EventRegistrationDomain {

    private Long id;

    private Long userId;

    private EventEntity event;

}
