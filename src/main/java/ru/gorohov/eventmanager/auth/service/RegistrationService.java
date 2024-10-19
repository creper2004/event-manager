package ru.gorohov.eventmanager.auth.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gorohov.eventmanager.auth.forms.SignUpRequest;
import ru.gorohov.eventmanager.user.api.UserDto;
import ru.gorohov.eventmanager.user.convector.UserConvector;
import ru.gorohov.eventmanager.user.domain.UserDomain;
import ru.gorohov.eventmanager.user.domain.UserRole;
import ru.gorohov.eventmanager.user.domain.UserService;

@Service
@RequiredArgsConstructor
public class RegistrationService {

    private final UserService userService;
    private static final Logger log = LoggerFactory.getLogger(RegistrationService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserConvector userConvector;


    public UserDto registerUser(SignUpRequest signUpRequest) {
        log.info("Registering service: {}", signUpRequest);
        var hashedPass = passwordEncoder.encode(signUpRequest.getPassword());
        var user = new UserDomain(null,
                signUpRequest.getLogin(),
                signUpRequest.getAge(),
                UserRole.USER,
                hashedPass
        );
        try {

            return userConvector.fromDomainToDto(userService.saveUser(user));

        }
        catch (EntityExistsException e) {
            throw new EntityExistsException(e.getMessage());
        }
    }

}