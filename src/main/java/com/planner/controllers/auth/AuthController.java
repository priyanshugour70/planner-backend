package com.planner.controllers.auth;

import com.planner.dtos.APIResponse;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.auth.*;
import com.planner.dtos.res.auth.AuthResponse;
import com.planner.dtos.res.auth.UserResponse;
import com.planner.service.auth.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth", description = "Authentication and user management")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<APIResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        ServiceResult<AuthResponse> result = authService.register(request);
        return toApiResponse(result, "Registration successful");
    }

    @PostMapping("/login")
    @Operation(summary = "Login with email and password")
    public ResponseEntity<APIResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        String ipAddress = httpRequest.getRemoteAddr();
        ServiceResult<AuthResponse> result = authService.login(request, ipAddress);
        return toApiResponse(result, "Login successful");
    }

    @PostMapping("/refresh-token")
    @Operation(summary = "Refresh access token using refresh token")
    public ResponseEntity<APIResponse<AuthResponse>> refreshToken(@Valid @RequestBody RefreshTokenRequest request) {
        ServiceResult<AuthResponse> result = authService.refreshToken(request);
        return toApiResponse(result, "Token refreshed successfully");
    }

    @PostMapping("/logout")
    @Operation(summary = "Logout current session")
    public ResponseEntity<APIResponse<Void>> logout(@RequestHeader("Authorization") String authHeader) {
        String token = extractToken(authHeader);
        ServiceResult<Void> result = authService.logout(token);
        return toApiResponse(result, "Logged out successfully");
    }

    @PostMapping("/logout-all")
    @Operation(summary = "Logout all sessions for the current user")
    public ResponseEntity<APIResponse<Void>> logoutAll() {
        ServiceResult<Void> result = authService.logoutAllSessions();
        return toApiResponse(result, "All sessions logged out successfully");
    }

    @PostMapping("/forgot-password")
    @Operation(summary = "Request a password reset email")
    public ResponseEntity<APIResponse<Void>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        ServiceResult<Void> result = authService.forgotPassword(request);
        return toApiResponse(result, "Password reset email sent");
    }

    @PostMapping("/reset-password")
    @Operation(summary = "Reset password using a reset token")
    public ResponseEntity<APIResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        ServiceResult<Void> result = authService.resetPassword(request);
        return toApiResponse(result, "Password reset successful");
    }

    @PutMapping("/change-password")
    @Operation(summary = "Change the current user's password")
    public ResponseEntity<APIResponse<Void>> changePassword(@Valid @RequestBody ChangePasswordRequest request) {
        ServiceResult<Void> result = authService.changePassword(request);
        return toApiResponse(result, "Password changed successfully");
    }

    @GetMapping("/me")
    @Operation(summary = "Get the current authenticated user's profile")
    public ResponseEntity<APIResponse<UserResponse>> me() {
        ServiceResult<UserResponse> result = authService.getCurrentUser();
        return toApiResponse(result, "Profile retrieved successfully");
    }

    @PutMapping("/profile")
    @Operation(summary = "Update the current user's profile")
    public ResponseEntity<APIResponse<UserResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        ServiceResult<UserResponse> result = authService.updateProfile(request);
        return toApiResponse(result, "Profile updated successfully");
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }

    private <T> ResponseEntity<APIResponse<T>> toApiResponse(ServiceResult<T> result, String message) {
        if (result.isSuccess()) {
            return ResponseEntity.ok(APIResponse.success(result.getData(), message));
        }
        return ResponseEntity.status(result.getStatus())
                .body(APIResponse.failure(result.getErrors(), result.getStatus(), message));
    }
}
