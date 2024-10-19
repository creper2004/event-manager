package ru.gorohov.eventmanager.location.api;

import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorohov.eventmanager.location.domaim.Location;
import ru.gorohov.eventmanager.location.domaim.LocationsService;
import ru.gorohov.eventmanager.location.convector.LocationConvector;

import java.util.List;

@RestController
@RequestMapping("/locations")
public class LocationsController {

    private final LocationsService locationsService;

    private final static Logger log = LoggerFactory.getLogger(LocationsController.class);

    private final LocationConvector locationConvector;

    @Autowired
    public LocationsController(LocationsService locationsService, LocationConvector locationConvector) {
        this.locationsService = locationsService;

        this.locationConvector = locationConvector;
    }

    @GetMapping
    public ResponseEntity<List<LocationDto>> getLocations() {
        log.info("Get request for get all locations");
        return ResponseEntity.status(200)
                .body(locationsService.getAllLocations()
                .stream().map(locationConvector::fromServiceToDto).toList());

    }

    @PostMapping
    public ResponseEntity<LocationDto> createLocation(@RequestBody @Valid LocationDto locationDto) {
        log.info("Post request for create a new location: locationDto = {}", locationDto);
        Location location = LocationConvector.fromDtoToService(locationDto);
        return ResponseEntity.status(201)
                .body(locationConvector.fromServiceToDto(locationsService.createLocation(location)));
    }

    @DeleteMapping("/{locationId}")
    public ResponseEntity<Void> deleteLocation(@PathVariable Long locationId) {
        log.info("Delete request for delete a location with id {}", locationId);
        Location location = locationsService.getLocationById(locationId);
        locationsService.deleteLocation(location);
        return ResponseEntity
                .status(HttpStatus.NO_CONTENT).build();
    }

    @GetMapping("/{locationId}")
    public ResponseEntity<LocationDto> getLocation(@PathVariable Long locationId) {
        log.info("Get request for get a location with id {}", locationId);
        Location location = locationsService.getLocationById(locationId);
        return ResponseEntity.status(200)
                .body(locationConvector.fromServiceToDto(location));
    }

    @PutMapping("/{locationId}")
    public ResponseEntity<LocationDto> updateLocation(@PathVariable Long locationId, @RequestBody @Valid LocationDto locationDto) {
        log.info("Put request for update a location with id={} and body={}", locationId, locationDto);
        Location location = LocationConvector.fromDtoToService(locationDto);
        return ResponseEntity.status(200)
                .body(locationConvector.fromServiceToDto(locationsService.updateLocation(locationId, location)));

    }
}
