package ru.gorohov.eventmanager.web;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.ServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(value = { MethodArgumentNotValidException.class })
    public ResponseEntity<ErrorMessageResponse> handleMethodArgumentNotValidException(final MethodArgumentNotValidException ex) {
        ErrorMessageResponse errorMessage = ErrorMessageResponse
                .builder()
                .message("Bad request")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle MethodArgumentNotValidException:{}", errorMessage);
        return ResponseEntity.status(400).body(errorMessage);
    }

    @ExceptionHandler(value = {IllegalArgumentException.class})
    public ResponseEntity<ErrorMessageResponse> handleIllegalArgumentException(final IllegalArgumentException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Bad request")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle IllegalArgumentException:{}", errorMessageResponse);
        return ResponseEntity.status(400).body(errorMessageResponse);
    }

    @ExceptionHandler(value = {EntityNotFoundException.class })
    public ResponseEntity<ErrorMessageResponse> handleEntityNotFoundException(final EntityNotFoundException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Not found")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle EntityNotFoundException:{}", errorMessageResponse);
        return ResponseEntity.status(404).body(errorMessageResponse);
    }

    @ExceptionHandler(value = {EntityExistsException.class})
    public ResponseEntity<ErrorMessageResponse> handleEntityExistsException(final EntityExistsException ex) {
        var message = ErrorMessageResponse.builder()
                .message("Conflict")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle EntityExistsException:{}", message);
        return ResponseEntity.status(409).body(message);
    }

    @ExceptionHandler(value = {BadCredentialsException.class })
    public ResponseEntity<ErrorMessageResponse> handleBadCredentialsException(final BadCredentialsException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Bad credentials")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle BadCredentialsException:{}", errorMessageResponse);
        return ResponseEntity.status(401).body(errorMessageResponse);
    }


    @ExceptionHandler(value = {AccessDeniedException.class })
    public ResponseEntity<ErrorMessageResponse> handleException(final AccessDeniedException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Access denied")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle Exception:{}", errorMessageResponse);
        return ResponseEntity.status(403).body(errorMessageResponse);
    }

    @ExceptionHandler(value = {RuntimeException.class})
    public ResponseEntity<ErrorMessageResponse> handleException(final RuntimeException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Runtime exception")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle Exception:{}", errorMessageResponse);
        return ResponseEntity.status(400).body(errorMessageResponse);
    }


    @ExceptionHandler(value = {SecurityException.class})
    public ResponseEntity<ErrorMessageResponse> handleSecurityException(final ServletException ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Unauthorized")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle Exception:{}", errorMessageResponse);
        return ResponseEntity.status(401).body(errorMessageResponse);
    }

    @ExceptionHandler(value = {Exception.class})
    public ResponseEntity<ErrorMessageResponse> handleException(final Exception ex) {
        ErrorMessageResponse errorMessageResponse = ErrorMessageResponse
                .builder()
                .message("Internal server error")
                .detailedMessage(ex.getMessage())
                .dateTime(LocalDateTime.now())
                .build();
        log.error("Handle Exception:{}", errorMessageResponse);
        return ResponseEntity.status(500).body(errorMessageResponse);
    }


}