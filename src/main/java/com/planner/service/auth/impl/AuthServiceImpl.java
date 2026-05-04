package com.planner.service.auth.impl;

import com.planner.dtos.ErrorResponse;
import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.auth.*;
import com.planner.dtos.res.auth.AuthResponse;
import com.planner.dtos.res.auth.UserResponse;
import com.planner.entities.auth.Session;
import com.planner.entities.auth.User;
import com.planner.repositories.auth.SessionRepository;
import com.planner.repositories.auth.UserRepository;
import com.planner.security.JwtTokenProvider;
import com.planner.security.SecurityUtils;
import com.planner.service.auth.AuthService;
import com.planner.service.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Override
    public ServiceResult<AuthResponse> register(RegisterRequest request) {
        log.info("Processing registration for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Registration failed - email already exists: {}", request.getEmail());
            return ServiceResult.fail(HttpStatus.CONFLICT,
                    List.of(ErrorResponse.of(HttpStatus.CONFLICT, "An account with this email already exists", "email")));
        }

        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail().toLowerCase().trim())
                .password(passwordEncoder.encode(request.getPassword()))
                .phoneNumber(request.getPhoneNumber())
                .dateOfBirth(request.getDateOfBirth())
                .gender(request.getGender())
                .occupation(request.getOccupation())
                .deviceId(request.getDeviceId())
                .isEmailVerified(false)
                .isOnboardingComplete(false)
                .build();
        user.setActive(true);

        User savedUser = userRepository.save(user);
        log.info("User registered successfully with id: {}", savedUser.getId());

        String verificationToken = jwtTokenProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail());
        emailService.sendVerificationEmail(savedUser.getEmail(), savedUser.getFirstName(), verificationToken);
        emailService.sendWelcomeEmail(savedUser.getEmail(), savedUser.getFirstName());

        String accessToken = jwtTokenProvider.generateAccessToken(savedUser.getId(), savedUser.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getId(), savedUser.getEmail());

        createSession(savedUser, accessToken, refreshToken, null, null);

        AuthResponse response = buildAuthResponse(savedUser, accessToken, refreshToken);
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult<AuthResponse> login(LoginRequest request, String ipAddress) {
        log.info("Processing login for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmailAndActiveTrue(request.getEmail().toLowerCase().trim());
        if (userOpt.isEmpty()) {
            log.warn("Login failed - user not found for email: {}", request.getEmail());
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid email or password")));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            log.warn("Login failed - invalid password for user id: {}", user.getId());
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid email or password")));
        }

        String accessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String refreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        createSession(user, accessToken, refreshToken, request.getDeviceInfo(), ipAddress);

        log.info("User logged in successfully - userId: {}", user.getId());
        AuthResponse response = buildAuthResponse(user, accessToken, refreshToken);
        return ServiceResult.ok(response);
    }

    @Override
    public ServiceResult<AuthResponse> refreshToken(RefreshTokenRequest request) {
        log.info("Processing token refresh");

        if (!jwtTokenProvider.validateToken(request.getRefreshToken())) {
            log.warn("Refresh token validation failed");
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid or expired refresh token")));
        }

        String tokenType = jwtTokenProvider.getTokenType(request.getRefreshToken());
        if (!"REFRESH".equals(tokenType)) {
            log.warn("Token is not a refresh token, type: {}", tokenType);
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Invalid token type")));
        }

        Optional<Session> sessionOpt = sessionRepository.findByRefreshTokenAndIsRevokedFalse(request.getRefreshToken());
        if (sessionOpt.isEmpty()) {
            log.warn("No active session found for refresh token");
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Session not found or already revoked")));
        }

        Session oldSession = sessionOpt.get();
        sessionRepository.revokeSessionById(oldSession.getId(), "TOKEN_REFRESH");

        Long userId = jwtTokenProvider.getUserIdFromToken(request.getRefreshToken());
        Optional<User> userOpt = userRepository.findByIdAndActiveTrue(userId);
        if (userOpt.isEmpty()) {
            log.warn("User not found for id: {} during token refresh", userId);
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "User account not found or deactivated")));
        }

        User user = userOpt.get();
        String newAccessToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(user.getId(), user.getEmail());

        createSession(user, newAccessToken, newRefreshToken, oldSession.getDeviceInfo(), oldSession.getIpAddress());

        log.info("Token refreshed successfully for userId: {}", user.getId());
        AuthResponse response = buildAuthResponse(user, newAccessToken, newRefreshToken);
        return ServiceResult.ok(response);
    }

    @Override
    @CacheEvict(value = "sessions", allEntries = true)
    public ServiceResult<Void> logout(String accessToken) {
        log.info("Processing logout");

        if (accessToken == null || accessToken.isBlank()) {
            return ServiceResult.fail(HttpStatus.BAD_REQUEST,
                    List.of(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Access token is required")));
        }

        String token = accessToken.startsWith("Bearer ") ? accessToken.substring(7) : accessToken;

        Optional<Session> sessionOpt = sessionRepository.findByAccessTokenAndIsRevokedFalse(token);
        if (sessionOpt.isEmpty()) {
            log.warn("Logout failed - no active session found for the provided token");
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "No active session found")));
        }

        Session session = sessionOpt.get();
        sessionRepository.revokeSessionById(session.getId(), "USER_LOGOUT");

        log.info("User logged out successfully - sessionId: {}", session.getId());
        return ServiceResult.ok(null);
    }

    @Override
    @CacheEvict(value = "sessions", allEntries = true)
    public ServiceResult<Void> logoutAllSessions() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            log.warn("Logout all sessions failed - no authenticated user");
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Not authenticated")));
        }

        int revokedCount = sessionRepository.revokeAllSessionsByUserId(userId, "USER_LOGOUT_ALL");
        log.info("All sessions revoked for userId: {} - count: {}", userId, revokedCount);
        return ServiceResult.ok(null);
    }

    @Override
    public ServiceResult<Void> forgotPassword(ForgotPasswordRequest request) {
        log.info("Processing forgot password for email: {}", request.getEmail());

        Optional<User> userOpt = userRepository.findByEmailAndActiveTrue(request.getEmail().toLowerCase().trim());
        if (userOpt.isEmpty()) {
            log.info("Forgot password requested for non-existent email: {} - returning success to prevent enumeration",
                    request.getEmail());
            return ServiceResult.ok(null);
        }

        User user = userOpt.get();
        String resetToken = jwtTokenProvider.generateAccessToken(user.getId(), user.getEmail());

        emailService.sendPasswordResetEmail(user.getEmail(), user.getFirstName(), resetToken);
        log.info("Password reset email sent for userId: {}", user.getId());
        return ServiceResult.ok(null);
    }

    @Override
    @CacheEvict(value = "sessions", allEntries = true)
    public ServiceResult<Void> resetPassword(ResetPasswordRequest request) {
        log.info("Processing password reset");

        if (!jwtTokenProvider.validateToken(request.getToken())) {
            log.warn("Password reset failed - invalid or expired token");
            return ServiceResult.fail(HttpStatus.BAD_REQUEST,
                    List.of(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Invalid or expired reset token")));
        }

        Long userId = jwtTokenProvider.getUserIdFromToken(request.getToken());
        Optional<User> userOpt = userRepository.findByIdAndActiveTrue(userId);
        if (userOpt.isEmpty()) {
            log.warn("Password reset failed - user not found for id: {}", userId);
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "User not found")));
        }

        User user = userOpt.get();
        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        sessionRepository.revokeAllSessionsByUserId(user.getId(), "PASSWORD_RESET");

        emailService.sendPasswordChangedNotification(user.getEmail(), user.getFirstName());
        log.info("Password reset successfully for userId: {}", user.getId());
        return ServiceResult.ok(null);
    }

    @Override
    @CacheEvict(value = "sessions", allEntries = true)
    public ServiceResult<Void> changePassword(ChangePasswordRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Not authenticated")));
        }

        Optional<User> userOpt = userRepository.findByIdAndActiveTrue(userId);
        if (userOpt.isEmpty()) {
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "User not found")));
        }

        User user = userOpt.get();

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            log.warn("Change password failed - current password mismatch for userId: {}", userId);
            return ServiceResult.fail(HttpStatus.BAD_REQUEST,
                    List.of(ErrorResponse.of(HttpStatus.BAD_REQUEST, "Current password is incorrect", "currentPassword")));
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        sessionRepository.revokeAllSessionsByUserId(userId, "PASSWORD_CHANGED");

        emailService.sendPasswordChangedNotification(user.getEmail(), user.getFirstName());
        log.info("Password changed successfully for userId: {}", userId);
        return ServiceResult.ok(null);
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceResult<UserResponse> getCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Not authenticated")));
        }

        Optional<User> userOpt = userRepository.findByIdAndActiveTrue(userId);
        if (userOpt.isEmpty()) {
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "User not found")));
        }

        return ServiceResult.ok(mapToUserResponse(userOpt.get()));
    }

    @Override
    public ServiceResult<UserResponse> updateProfile(UpdateProfileRequest request) {
        Long userId = SecurityUtils.getCurrentUserId();
        if (userId == null) {
            return ServiceResult.fail(HttpStatus.UNAUTHORIZED,
                    List.of(ErrorResponse.of(HttpStatus.UNAUTHORIZED, "Not authenticated")));
        }

        Optional<User> userOpt = userRepository.findByIdAndActiveTrue(userId);
        if (userOpt.isEmpty()) {
            return ServiceResult.fail(HttpStatus.NOT_FOUND,
                    List.of(ErrorResponse.of(HttpStatus.NOT_FOUND, "User not found")));
        }

        User user = userOpt.get();

        if (request.getFirstName() != null) {
            user.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null) {
            user.setLastName(request.getLastName());
        }
        if (request.getPhoneNumber() != null) {
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getDateOfBirth() != null) {
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getOccupation() != null) {
            user.setOccupation(request.getOccupation());
        }
        if (request.getProfileImageUrl() != null) {
            user.setProfileImageUrl(request.getProfileImageUrl());
        }
        if (request.getIsOnboardingComplete() != null) {
            user.setIsOnboardingComplete(request.getIsOnboardingComplete());
        }

        User updatedUser = userRepository.save(user);
        log.info("Profile updated successfully for userId: {}", userId);
        return ServiceResult.ok(mapToUserResponse(updatedUser));
    }

    private void createSession(User user, String accessToken, String refreshToken,
                               String deviceInfo, String ipAddress) {
        LocalDateTime expiresAt = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(System.currentTimeMillis() + jwtTokenProvider.getRefreshTokenExpiryMs()),
                ZoneId.systemDefault()
        );

        Session session = Session.builder()
                .user(user)
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .deviceInfo(deviceInfo)
                .ipAddress(ipAddress)
                .isRevoked(false)
                .expiresAt(expiresAt)
                .lastActivityAt(LocalDateTime.now())
                .build();
        session.setActive(true);

        sessionRepository.save(session);
        log.debug("Session created for userId: {}", user.getId());
    }

    private AuthResponse buildAuthResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .expiresIn(jwtTokenProvider.getAccessTokenExpiryMs() / 1000)
                .user(mapToUserResponse(user))
                .build();
    }

    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .occupation(user.getOccupation())
                .profileImageUrl(user.getProfileImageUrl())
                .isEmailVerified(user.getIsEmailVerified())
                .isOnboardingComplete(user.getIsOnboardingComplete())
                .build();
    }
}
