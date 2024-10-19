package ru.gorohov.eventmanager.auth.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import ru.gorohov.eventmanager.auth.forms.JwtTokenResponse;
import ru.gorohov.eventmanager.auth.forms.SignInRequest;
import ru.gorohov.eventmanager.secured.jwt.JwtManager;
import ru.gorohov.eventmanager.user.domain.UserService;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtManager jwtManager;

    public JwtTokenResponse authenticate(SignInRequest signInRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    signInRequest.getLogin(), signInRequest.getPassword()
            ));
        }
        catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad credentials", e);
        }
        UserDetails userDetails = userService.loadUserByUsername(signInRequest.getLogin());
        String token = jwtManager.generateToken(userDetails);
        return new JwtTokenResponse(token);
    }

}