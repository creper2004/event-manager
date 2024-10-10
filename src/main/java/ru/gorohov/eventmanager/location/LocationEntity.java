package ru.gorohov.eventmanager.location;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="locations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LocationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

    @Column(name =  "capacity")
    private Integer capacity;

    @Column(name = "description")
    private String description;
}
