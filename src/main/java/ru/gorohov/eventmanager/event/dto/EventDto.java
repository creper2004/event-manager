package ru.gorohov.eventmanager.event.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
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
