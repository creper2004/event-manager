package ru.gorohov.eventmanager.location;

import org.springframework.stereotype.Component;

@Component
public class LocationConvector {

    public LocationDto fromServiceToDto(Location location) {
        return LocationDto.builder()
                .id(location.getId())
                .name(location.getName())
                .capacity(location.getCapacity())
                .address(location.getAddress())
                .description(location.getDescription())
                .build();
    }

    public LocationEntity fromServiceToEntity(Location location) {
        return LocationEntity.builder()
                .id(location.getId())
                .address(location.getAddress())
                .description(location.getDescription())
                .capacity(location.getCapacity())
                .name(location.getName())
                .build();
    }

    public Location fromEntityToService(LocationEntity locationEntity) {
        return Location.builder()
                .id(locationEntity.getId())
                .address(locationEntity.getAddress())
                .description(locationEntity.getDescription())
                .capacity(locationEntity.getCapacity())
                .name(locationEntity.getName())
                .build();
    }

    public static Location fromDtoToService(LocationDto locationDto) {
        return Location.builder()
                .id(locationDto.getId())
                .address(locationDto.getAddress())
                .description(locationDto.getDescription())
                .capacity(locationDto.getCapacity())
                .name(locationDto.getName())
                .build();
    }

}
