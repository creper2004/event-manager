package ru.gorohov.eventmanager.location;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class LocationDto {

    private Long id;

    @NotBlank(message = "Location name should not be blank")
    @NotNull(message = "Location name should not be null")
    @Size(max = 500, message = "Maximum size of location name is 500 characters")
    private String name;

    @NotBlank(message = "Location address should not be blank")
    @NotNull(message = "Location address should not be null")
    @Size(max = 500, message = "Maximum size of location address is 500 characters")
    private String address;

    @Min(value = 5, message = "Location capacity should be greater than 5")
    @NotNull(message = "Location capacity should not be null")
    private Integer capacity;


    @Size(max = 1000, message = "Maximum size of location description is 1000 characters")
    private String description;
}
