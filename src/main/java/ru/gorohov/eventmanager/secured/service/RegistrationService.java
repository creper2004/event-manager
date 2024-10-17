package ru.gorohov.eventmanager.secured.service;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.gorohov.eventmanager.secured.convector.UserConvector;
import ru.gorohov.eventmanager.secured.forms.SignUpRequest;
import ru.gorohov.eventmanager.secured.user.UserDomain;
import ru.gorohov.eventmanager.secured.user.UserDto;
import ru.gorohov.eventmanager.secured.user.UserRole;

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