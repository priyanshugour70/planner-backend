package com.planner.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenProviderTest {

    private JwtTokenProvider jwtTokenProvider;

    @BeforeEach
    void setUp() {
        String secret = "testSecretKeyForJWTTokenGeneration2024SecureRandomStringTestOnly";
        jwtTokenProvider = new JwtTokenProvider(secret, 86400000L, 604800000L);
    }

    @Test
    @DisplayName("Generate and validate access token")
    void generateAndValidateAccessToken() {
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getUserIdFromToken(token)).isEqualTo(1L);
        assertThat(jwtTokenProvider.getEmailFromToken(token)).isEqualTo("test@example.com");
        assertThat(jwtTokenProvider.getTokenType(token)).isEqualTo("ACCESS");
    }

    @Test
    @DisplayName("Generate and validate refresh token")
    void generateAndValidateRefreshToken() {
        String token = jwtTokenProvider.generateRefreshToken(1L, "test@example.com");

        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.validateToken(token)).isTrue();
        assertThat(jwtTokenProvider.getTokenType(token)).isEqualTo("REFRESH");
    }

    @Test
    @DisplayName("Invalid token returns false")
    void invalidToken_ReturnsFalse() {
        assertThat(jwtTokenProvider.validateToken("invalid.token.here")).isFalse();
        assertThat(jwtTokenProvider.validateToken("")).isFalse();
        assertThat(jwtTokenProvider.validateToken(null)).isFalse();
    }

    @Test
    @DisplayName("Token expiry is set correctly")
    void tokenExpiryIsCorrect() {
        String token = jwtTokenProvider.generateAccessToken(1L, "test@example.com");
        long expiry = jwtTokenProvider.getExpirationFromToken(token).getTime();
        long now = System.currentTimeMillis();

        assertThat(expiry).isGreaterThan(now);
        assertThat(expiry - now).isLessThanOrEqualTo(86400000L);
    }
}
