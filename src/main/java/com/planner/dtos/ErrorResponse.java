package com.planner.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ErrorResponse {

    private HttpStatus status;
    private String message;
    private LocalDateTime timestamp;
    private String field;
    private Integer code;

    public static ErrorResponse of(HttpStatus status, String message) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .timestamp(LocalDateTime.now())
                .code(status.value())
                .build();
    }

    public static ErrorResponse of(HttpStatus status, String message, String field) {
        return ErrorResponse.builder()
                .status(status)
                .message(message)
                .field(field)
                .timestamp(LocalDateTime.now())
                .code(status.value())
                .build();
    }
}
