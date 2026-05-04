package com.planner.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServiceResult<T> {

    private boolean success;
    private T data;
    private List<ErrorResponse> errors;
    private HttpStatus status;

    public static <T> ServiceResult<T> ok(T data) {
        return ServiceResult.<T>builder()
                .success(true)
                .data(data)
                .status(HttpStatus.OK)
                .build();
    }

    public static <T> ServiceResult<T> fail(HttpStatus status, List<ErrorResponse> errors) {
        return ServiceResult.<T>builder()
                .success(false)
                .status(status)
                .errors(errors)
                .build();
    }
}
