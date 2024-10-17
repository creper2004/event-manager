package ru.gorohov.eventmanager.web;

import com.fasterxml.jackson.annotation.JsonFormat;
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

    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private LocalDateTime dateTime;
}
