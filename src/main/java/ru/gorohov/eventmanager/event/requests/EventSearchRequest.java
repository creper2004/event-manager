package ru.gorohov.eventmanager.event.requests;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class EventSearchRequest {

    private String name;
    private Integer placesMin;
    private Integer placesMax;
    private LocalDateTime dateStartAfter;
    private LocalDateTime dateStartBefore;
    private Integer costMin;
    private Integer costMax;
    private Integer durationMin;
    private Integer durationMax;
    private Long locationId;
    private String eventStatus;
}
