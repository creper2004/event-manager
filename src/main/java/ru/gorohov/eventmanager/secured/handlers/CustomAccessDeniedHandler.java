package ru.gorohov.eventmanager.secured.handlers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import ru.gorohov.eventmanager.web.ErrorMessageResponse;

import java.io.IOException;
import java.time.LocalDateTime;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final static Logger log = LoggerFactory.getLogger(CustomAccessDeniedHandler.class);
    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException)
            throws IOException, ServletException {

        log.error("Handling access denied exception", accessDeniedException);

        var message = ErrorMessageResponse.builder()
                .message("Forbidden")
                .detailedMessage(accessDeniedException.getMessage())
                .dateTime(LocalDateTime.now())
                .build();

        var stringResponse = objectMapper.writeValueAsString(message);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(stringResponse);
    }
}
