package com.medipet.medipet.user.infrastructure.outbound.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class JwtTokenProviderTest {

    private JwtTokenProvider tokenProvider;

    private static final String SECRET = "test-secret-key-for-unit-tests-only-32-chars!!";
    private static final long ACCESS_EXPIRATION = 900000L;
    private static final long REFRESH_EXPIRATION = 604800000L;

    @BeforeEach
    void setUp() {
        tokenProvider = new JwtTokenProvider(SECRET, ACCESS_EXPIRATION, REFRESH_EXPIRATION);
    }

    @Test
    void shouldGenerateAndExtractAccessToken() {
        String token = tokenProvider.generateAccessToken("user@example.com", "VETERINARIO");

        assertNotNull(token);
        assertEquals("user@example.com", tokenProvider.extractEmail(token));
        assertEquals("VETERINARIO", tokenProvider.extractRole(token));
    }

    @Test
    void shouldGenerateAndExtractRefreshToken() {
        String token = tokenProvider.generateRefreshToken("user@example.com");

        assertNotNull(token);
        assertEquals("user@example.com", tokenProvider.extractEmail(token));
    }

    @Test
    void shouldValidateAccessToken() {
        String token = tokenProvider.generateAccessToken("user@example.com", "ADMIN");

        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void shouldValidateRefreshToken() {
        String token = tokenProvider.generateRefreshToken("user@example.com");

        assertTrue(tokenProvider.validateToken(token));
    }

    @Test
    void shouldReturnFalseForInvalidToken() {
        assertFalse(tokenProvider.validateToken("totally.invalid.token"));
    }

    @Test
    void shouldReturnFalseForTamperedToken() {
        String token = tokenProvider.generateAccessToken("user@example.com", "ADMIN");
        String tampered = token + "x";

        assertFalse(tokenProvider.validateToken(tampered));
    }

    @Test
    void shouldExtractDifferentEmails() {
        String token1 = tokenProvider.generateAccessToken("user1@example.com", "ADMIN");
        String token2 = tokenProvider.generateAccessToken("user2@example.com", "ADMIN");

        assertEquals("user1@example.com", tokenProvider.extractEmail(token1));
        assertEquals("user2@example.com", tokenProvider.extractEmail(token2));
    }
}
