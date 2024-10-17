package ru.gorohov.eventmanager.location;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class LocationsService {


    private final LocationsRepository locationsRepository;
    private final LocationConvector locationConvector;

    @Autowired
    public LocationsService(LocationsRepository locationsRepository, LocationConvector locationConvector) {
        this.locationsRepository = locationsRepository;
        this.locationConvector = locationConvector;
    }

    public List<Location> getAllLocations() {
        return locationsRepository.findAll().stream()
                .map(locationConvector::fromEntityToService)
                .toList();
    }

    @Transactional
    public Location createLocation(Location location) {
        if (location.getId() != null) {
            throw new IllegalArgumentException("Location ID should be null while trying to create a new location");
        }
        return locationConvector.fromEntityToService(locationsRepository
                .save(locationConvector.fromServiceToEntity(location)));
    }

    @Transactional
    public void deleteLocation(Location location) {
        if (!locationsRepository.existsById(location.getId())) {
            throw new EntityNotFoundException("There is no location with id: " + location.getId());
        }
        locationsRepository.delete(locationConvector.fromServiceToEntity(location));
    }

    public Location getLocationById(Long id) {
        LocationEntity locationEntity = locationsRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no location with id: " + id));
        return locationConvector.fromEntityToService(locationEntity);
    }

    @Transactional
    public Location updateLocation(Long id, Location location) {
        if (location.getId() != null) {
            throw new IllegalArgumentException("Location ID should be null in body");
        }
        Location locationFound = getLocationById(id);
        if (locationFound.getCapacity() > location.getCapacity()) {
            throw new IllegalArgumentException("Capacity should not be less than it was");
        }
        locationFound.setAddress(location.getAddress());
        locationFound.setDescription(location.getDescription());
        locationFound.setName(location.getName());
        locationFound.setCapacity(location.getCapacity());
        var updatedLocation = locationsRepository.save(locationConvector.fromServiceToEntity(locationFound));
        return locationConvector.fromEntityToService(updatedLocation);
    }


}
