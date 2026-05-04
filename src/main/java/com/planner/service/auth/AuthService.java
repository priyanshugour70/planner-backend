package com.planner.service.auth;

import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.auth.*;
import com.planner.dtos.res.auth.AuthResponse;
import com.planner.dtos.res.auth.UserResponse;

public interface AuthService {

    ServiceResult<AuthResponse> register(RegisterRequest request);

    ServiceResult<AuthResponse> login(LoginRequest request, String ipAddress);

    ServiceResult<AuthResponse> refreshToken(RefreshTokenRequest request);

    ServiceResult<Void> logout(String accessToken);

    ServiceResult<Void> logoutAllSessions();

    ServiceResult<Void> forgotPassword(ForgotPasswordRequest request);

    ServiceResult<Void> resetPassword(ResetPasswordRequest request);

    ServiceResult<Void> changePassword(ChangePasswordRequest request);

    ServiceResult<UserResponse> getCurrentUser();

    ServiceResult<UserResponse> updateProfile(UpdateProfileRequest request);

    ServiceResult<Void> sendOtp(SendOtpRequest request);

    ServiceResult<AuthResponse> verifyOtp(VerifyOtpRequest request, String ipAddress);

    ServiceResult<AuthResponse> guestLogin(GuestLoginRequest request, String ipAddress);
}
