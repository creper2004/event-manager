package ru.gorohov.eventmanager.event.api;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {
    private Long id;
    private String name;
    private Long ownerId;
    private Integer maxPlaces;
    private Integer occupiedPlaces;
    private LocalDateTime date;
    private Integer cost;
    private Integer duration;
    private Long locationId;
    private String status;
}
