package com.planner.service.auth;

import com.planner.dtos.ServiceResult;
import com.planner.dtos.req.auth.LoginRequest;
import com.planner.dtos.req.auth.RegisterRequest;
import com.planner.dtos.res.auth.AuthResponse;
import com.planner.entities.auth.User;
import com.planner.enums.Gender;
import com.planner.repositories.auth.SessionRepository;
import com.planner.repositories.auth.UserRepository;
import com.planner.security.JwtTokenProvider;
import com.planner.service.auth.impl.AuthServiceImpl;
import com.planner.service.email.EmailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private AuthServiceImpl authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstName("Test");
        registerRequest.setLastName("User");
        registerRequest.setEmail("test@example.com");
        registerRequest.setPassword("password123");
        registerRequest.setGender(Gender.MALE);

        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        testUser = User.builder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .password("$2a$12$encodedPassword")
                .gender(Gender.MALE)
                .isEmailVerified(false)
                .isOnboardingComplete(false)
                .build();
        testUser.setId(1L);
        testUser.setActive(true);
    }

    @Test
    @DisplayName("Register - Success")
    void register_Success() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("$2a$12$encodedPassword");
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L, "test@example.com")).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiryMs()).thenReturn(86400000L);
        when(sessionRepository.save(any())).thenReturn(null);

        ServiceResult<AuthResponse> result = authService.register(registerRequest);

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getAccessToken()).isEqualTo("access-token");
        assertThat(result.getData().getRefreshToken()).isEqualTo("refresh-token");
        assertThat(result.getData().getUser().getEmail()).isEqualTo("test@example.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Register - Duplicate Email")
    void register_DuplicateEmail() {
        when(userRepository.existsByEmail("test@example.com")).thenReturn(true);

        ServiceResult<AuthResponse> result = authService.register(registerRequest);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.CONFLICT);
        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Login - Success")
    void login_Success() {
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$12$encodedPassword")).thenReturn(true);
        when(jwtTokenProvider.generateAccessToken(1L, "test@example.com")).thenReturn("access-token");
        when(jwtTokenProvider.generateRefreshToken(1L, "test@example.com")).thenReturn("refresh-token");
        when(jwtTokenProvider.getAccessTokenExpiryMs()).thenReturn(86400000L);
        when(sessionRepository.save(any())).thenReturn(null);

        ServiceResult<AuthResponse> result = authService.login(loginRequest, "127.0.0.1");

        assertThat(result.isSuccess()).isTrue();
        assertThat(result.getData().getAccessToken()).isEqualTo("access-token");
    }

    @Test
    @DisplayName("Login - Invalid Credentials")
    void login_InvalidCredentials() {
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$12$encodedPassword")).thenReturn(false);

        ServiceResult<AuthResponse> result = authService.login(loginRequest, "127.0.0.1");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    @DisplayName("Login - User Not Found")
    void login_UserNotFound() {
        when(userRepository.findByEmailAndActiveTrue("test@example.com")).thenReturn(Optional.empty());

        ServiceResult<AuthResponse> result = authService.login(loginRequest, "127.0.0.1");

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }
}
