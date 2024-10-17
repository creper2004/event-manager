package ru.gorohov.eventmanager.event.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class EventCreateRequest {

    @NotBlank(message = "Name of event should not be blank")
    private String name;

    //@NotBlank(message = "Max places of event should not be blank")
    @Min(value = 1, message = "Minimum places is 1")
    private Integer maxPlaces;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
    @FutureOrPresent(message = "Date should not be in past")
    private LocalDateTime date;

    @Min(value = 0, message = "Minimum cost of event is 0")
    private Integer cost;

    @Min(value = 30, message = "Minimal duration is 30 minutes")
    private Integer duration;

    @NotNull(message = "Location ID is mandatory")
    @Min(value = 0, message = "Invalid ID")
    private Long locationId;

}