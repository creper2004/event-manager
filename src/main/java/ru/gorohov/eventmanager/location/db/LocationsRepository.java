package ru.gorohov.eventmanager.location.db;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LocationsRepository extends JpaRepository<LocationEntity, Long> {
    Optional<LocationEntity> findById(Long id);

}
