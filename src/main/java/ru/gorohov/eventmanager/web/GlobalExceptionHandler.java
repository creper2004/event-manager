package ru.gorohov.eventmanager.web;

import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


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