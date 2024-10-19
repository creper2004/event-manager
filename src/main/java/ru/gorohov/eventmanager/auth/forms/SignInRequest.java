package ru.gorohov.eventmanager.auth.forms;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class SignInRequest {

    @NotBlank(message = "Login should not be empty")
    @Size(min = 3, message = "Login length should be larger than 2 characters")
    private String login;

    @NotBlank(message = "Password should not be empty")
    @Size(min = 3, message = "Password length should be larger than 2 characters")
    private String password;
}
