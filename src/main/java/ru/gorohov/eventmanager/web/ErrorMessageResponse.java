package ru.gorohov.eventmanager.web;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorMessageResponse {
    private String message;
    private String detailedMessage;
    private LocalDateTime dateTime;
}
