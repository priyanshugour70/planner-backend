package com.planner.dtos;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class APIResponse<T> {

    private String message;
    private HttpStatus status;
    private T data;
    private List<ErrorResponse> errors;
    private LocalDateTime timestamp;

    public static <T> APIResponse<T> success(T data, String message) {
        return APIResponse.<T>builder()
                .message(message)
                .status(HttpStatus.OK)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> APIResponse<T> failure(List<ErrorResponse> errors, HttpStatus status, String message) {
        return APIResponse.<T>builder()
                .message(message)
                .status(status)
                .errors(errors)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
