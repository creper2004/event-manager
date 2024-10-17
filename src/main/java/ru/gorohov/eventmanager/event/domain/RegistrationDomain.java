package ru.gorohov.eventmanager.event.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import ru.gorohov.eventmanager.event.entities.EventEntity;

@Data
@Builder
public class RegistrationDomain {

    private Long id;

    private EventEntity event;

}
