package ru.gorohov.eventmanager.event.entities;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "events")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @OneToMany(mappedBy = "event")
    private List<EventRegistrationEntity> registration = new ArrayList<EventRegistrationEntity>();

    @Column(name = "maxPlaces")
    private Integer maxPlaces;

    @Column(name = "date")
    private LocalDateTime date;

    @Column(name = "duration")
    private Integer duration;

    @Column(name = "cost")
    private Integer cost;

    @Column(name = "locationId")
    private Long locationId;

    @Column(name = "ownerId")
    private Long ownerId;

    @Column(name = "status")
    private String status;

}