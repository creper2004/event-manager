package ru.gorohov.eventmanager.secured.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.web.ErrorMessageResponse;

import java.io.IOException;
import java.time.LocalDateTime;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class CustomEntryPoint implements AuthenticationEntryPoint {

    private static final Logger log = LoggerFactory.getLogger(AuthenticationEntryPoint.class);
    private final ObjectMapper objectMapper;

    public CustomEntryPoint() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException)
            throws IOException, ServletException {
        log.error("Handling authentication error", authException);


        var errorMessage = ErrorMessageResponse.builder()
                .message("Failed to authenticate user")
                .dateTime(LocalDateTime.now())
                .detailedMessage(authException.getMessage())
                .build();

        var stringResponse = objectMapper.writeValueAsString(errorMessage);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(stringResponse);
    }
}
