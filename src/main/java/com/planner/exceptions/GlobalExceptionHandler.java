package com.planner.exceptions;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse<?>> handleResourceNotFound(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, ex.getMessage())),
                        HttpStatus.NOT_FOUND, "Resource not found"));
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<APIResponse<?>> handleBusinessException(BusinessException ex) {
        log.warn("Business exception: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus())
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(ex.getStatus(), ex.getMessage())),
                        ex.getStatus(), "Business error"));
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<APIResponse<?>> handleDuplicateResource(DuplicateResourceException ex) {
        log.warn("Duplicate resource: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.CONFLICT, ex.getMessage())),
                        HttpStatus.CONFLICT, "Duplicate resource"));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<APIResponse<?>> handleUnauthorized(UnauthorizedException ex) {
        log.warn("Unauthorized: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, ex.getMessage())),
                        HttpStatus.UNAUTHORIZED, "Unauthorized"));
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<APIResponse<?>> handleBadCredentials(BadCredentialsException ex) {
        log.warn("Bad credentials: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid email or password")),
                        HttpStatus.UNAUTHORIZED, "Authentication failed"));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<APIResponse<?>> handleAccessDenied(AccessDeniedException ex) {
        log.warn("Access denied: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.FORBIDDEN, ex.getMessage())),
                        HttpStatus.FORBIDDEN, "Access denied"));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<APIResponse<?>> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorResponse> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(this::mapFieldError)
                .toList();
        log.warn("Validation failed: {} errors", errors.size());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(APIResponse.failure(errors, HttpStatus.BAD_REQUEST, "Validation failed"));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<APIResponse<?>> handleNoHandlerFound(NoHandlerFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "Endpoint not found: " + ex.getRequestURL())),
                        HttpStatus.NOT_FOUND, "Not found"));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<APIResponse<?>> handleGenericException(Exception ex) {
        log.error("Unhandled exception: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(APIResponse.failure(
                        List.of(ErrorResponse.of(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred")),
                        HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error"));
    }

    private ErrorResponse mapFieldError(FieldError fieldError) {
        return ErrorResponse.of(HttpStatus.BAD_REQUEST, fieldError.getDefaultMessage(), fieldError.getField());
    }
}
