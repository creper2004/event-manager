package ru.gorohov.eventmanager.web;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.secured.UserSecurity;

@Component
@RequiredArgsConstructor
public class GetCurrentUserService {

    private final Logger log = LoggerFactory.getLogger(GetCurrentUserService.class);

    public UserSecurity getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            log.info("Getting current user");
            return (UserSecurity) authentication.getPrincipal();
        } else {
            throw new SecurityException("You do not have permission to do this");
        }

    }
}
