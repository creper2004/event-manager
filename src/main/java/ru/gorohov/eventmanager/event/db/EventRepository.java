package ru.gorohov.eventmanager.event.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<EventEntity> findById(Long id);

    @Query("""
        SELECT e FROM EventEntity e
        WHERE (:name IS NULL OR e.name LIKE %:name%)
        AND (:placesMin IS NULL OR e.maxPlaces >= :placesMin)
        AND (:placesMax IS NULL OR e.maxPlaces <= :placesMax)
        AND (CAST(:dateStartAfter as DATE) IS NULL OR e.date >= :dateStartAfter)
        AND (CAST(:dateStartBefore as DATE) IS NULL OR e.date <= :dateStartBefore)
        AND (:costMin IS NULL OR e.cost >= :costMin)
        AND (:costMax IS NULL OR e.cost <= :costMax)
        AND (:durationMin IS NULL OR e.duration >= :durationMin)
        AND (:durationMax IS NULL OR e.duration <= :durationMax)
        AND (:locationId IS NULL OR e.locationId = :locationId)
        AND (:eventStatus IS NULL OR e.status = :eventStatus)
       """)
    List<EventEntity> findEvents(@Param("name") String name,
                                 @Param("placesMin") Integer placesMin,
                                 @Param("placesMax") Integer placesMax,
                                 @Param("dateStartAfter") LocalDateTime dateStartAfter,
                                 @Param("dateStartBefore") LocalDateTime dateStartBefore,
                                 @Param("costMin") Integer costMin,
                                 @Param("costMax") Integer costMax,
                                 @Param("durationMin") Integer durationMin,
                                 @Param("durationMax") Integer durationMax,
                                 @Param("locationId") Long locationId,
                                 @Param("eventStatus") String eventStatus);

    List<EventEntity> findEventByOwnerId(Long ownerId);


    @Query(value = """
    SELECT * FROM events e
    WHERE e.location_id = :locationId
    AND e.status != 'CANCELLED'
    AND 
    (((:start <= e.date) AND (:finish >= e.date))
    OR ((:start >= e.date) AND (:finish <= e.date + make_interval(mins := e.duration)))
    OR ((:start >= e.date) AND (:start <= e.date + make_interval(mins := e.duration)) AND (:finish >= e.date + make_interval(mins := e.duration))))
    """, nativeQuery = true)
    List<EventEntity> findEventsByLocationIdAndInterval(@Param("locationId") Long locationId,
                                                        @Param("start") LocalDateTime start,
                                                        @Param("finish") LocalDateTime finish);


    @Modifying
    @Query("""
        UPDATE EventEntity e
        SET e.status = 'STARTED'
        WHERE e.status = 'WAIT_START'
        AND e.date <= CURRENT_TIMESTAMP
        """)
    void updateUpcomingEvents();

    @Modifying
    @Query(value = """
    UPDATE events AS e 
    SET status = 'FINISHED'
    WHERE status = 'STARTED'
    AND e.date + make_interval(mins := e.duration) <= CURRENT_TIMESTAMP
    """, nativeQuery = true)
    void updateFinishingEvents();

}