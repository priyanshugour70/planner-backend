package com.planner.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.planner.dtos.APIResponse;
import com.planner.dtos.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        log.error("Unauthorized access attempt: {}", authException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        APIResponse<?> apiResponse = APIResponse.failure(
                List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Unauthorized: " + authException.getMessage())),
                HttpStatus.UNAUTHORIZED,
                "Authentication required"
        );
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
