package ru.gorohov.eventmanager.user.api;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.gorohov.eventmanager.auth.forms.JwtTokenResponse;
import ru.gorohov.eventmanager.auth.forms.SignInRequest;
import ru.gorohov.eventmanager.auth.forms.SignUpRequest;
import ru.gorohov.eventmanager.auth.service.AuthenticationService;
import ru.gorohov.eventmanager.auth.service.RegistrationService;
import ru.gorohov.eventmanager.user.convector.UserConvector;
import ru.gorohov.eventmanager.user.domain.UserService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {

    private final AuthenticationService authenticationService;
    private final UserService userService;
    private final RegistrationService registrationService;
    private final UserConvector userConvector;
    private static final Logger logger= LoggerFactory.getLogger(UserController.class);

    @PostMapping()
    public ResponseEntity<UserDto> register(@RequestBody @Valid SignUpRequest signUpRequest) {
        logger.info("Registering request {}", signUpRequest);
        return ResponseEntity.status(201)
                .body(registrationService.registerUser(signUpRequest));
    }

    @PostMapping("/auth")
    public ResponseEntity<JwtTokenResponse> login(@RequestBody @Valid SignInRequest signInRequest) {
        logger.info("Auth request {}", signInRequest);
        return ResponseEntity.ok(authenticationService.authenticate(signInRequest));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        logger.info("Getting user by id {}", id);
        var user = userService.getUserById(id);
        return ResponseEntity.status(200)
                .body(userConvector.fromDomainToDto(user));
    }


}