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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        log.error("Access denied: {}", accessDeniedException.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);

        APIResponse<?> apiResponse = APIResponse.failure(
                List.of(ErrorResponse.of(HttpStatus.FORBIDDEN, "Access denied: " + accessDeniedException.getMessage())),
                HttpStatus.FORBIDDEN,
                "Access denied"
        );
        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
