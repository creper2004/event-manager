package ru.gorohov.eventmanager.location;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class Location {
    private Long id;
    private String name;
    private String address;
    private Integer capacity;
    private String description;
}
