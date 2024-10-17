package ru.gorohov.eventmanager.secured.forms;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignUpRequest {

    @NotBlank(message = "Login should not be empty")
    @Size(min = 3, message = "Login length should be larger than 2 characters")
    private String login;

    @Min(value = 18, message = "Minimum age is 18 y.o.")
    private Integer age;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 3, message = "Password length should be larger than 2 characters")
    private String password;
}
