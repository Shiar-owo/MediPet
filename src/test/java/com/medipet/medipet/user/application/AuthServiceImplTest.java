package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.shared.exception.UnauthorizedException;
import com.medipet.medipet.user.domain.model.RefreshToken;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.RefreshTokenRepository;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private TokenProvider tokenProvider;

    private AuthServiceImpl authService;

    private static final long REFRESH_EXPIRATION_MS = 604800000L;

    @BeforeEach
    void setUp() {
        authService = new AuthServiceImpl(userRepository, refreshTokenRepository,
                tokenProvider, REFRESH_EXPIRATION_MS);
    }

    @Test
    void shouldIssueTokensSuccessfully() {
        User user = User.withId(
                com.medipet.medipet.user.domain.valueobject.UserId.generate(),
                "test@example.com", "hashed", "John", "Doe",
                Role.VETERINARIO, true, null, null, null, null, null);

        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(tokenProvider.generateAccessToken("test@example.com", "VETERINARIO")).thenReturn("access-token");
        when(tokenProvider.generateRefreshToken("test@example.com")).thenReturn("refresh-token");

        var result = authService.issueTokens("test@example.com");

        assertEquals(user.getId().getValue().toString(), result.userId());
        assertEquals("test@example.com", result.email());
        assertEquals("access-token", result.accessToken());
        assertEquals("refresh-token", result.refreshToken());
        assertNotNull(user.getLastLoginAt());
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> authService.issueTokens("missing@example.com"));
    }

    @Test
    void shouldRefreshTokenSuccessfully() {
        RefreshToken storedToken = RefreshToken.create("old-refresh", "test@example.com",
                LocalDateTime.now().plusDays(7));
        User user = User.withId(
                com.medipet.medipet.user.domain.valueobject.UserId.generate(),
                "test@example.com", "hashed", "John", "Doe",
                Role.VETERINARIO, true, null, null, null, null, null);

        when(tokenProvider.validateToken("old-refresh")).thenReturn(true);
        when(refreshTokenRepository.findByToken("old-refresh")).thenReturn(Optional.of(storedToken));
        when(userRepository.findByEmail("test@example.com")).thenReturn(Optional.of(user));
        when(tokenProvider.generateAccessToken("test@example.com", "VETERINARIO")).thenReturn("new-access");
        when(tokenProvider.generateRefreshToken("test@example.com")).thenReturn("new-refresh");

        var result = authService.refreshToken("old-refresh");

        assertEquals(user.getId().getValue().toString(), result.userId());
        assertEquals("test@example.com", result.email());
        assertEquals("new-access", result.accessToken());
        assertEquals("new-refresh", result.refreshToken());
        verify(refreshTokenRepository).deleteByToken("old-refresh");
        verify(refreshTokenRepository).save(any(RefreshToken.class));
    }

    @Test
    void shouldThrowWhenRefreshTokenInvalid() {
        when(tokenProvider.validateToken("bad-token")).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken("bad-token"));
    }

    @Test
    void shouldThrowWhenRefreshTokenNotFound() {
        when(tokenProvider.validateToken("valid-but-missing")).thenReturn(true);
        when(refreshTokenRepository.findByToken("valid-but-missing")).thenReturn(Optional.empty());

        assertThrows(UnauthorizedException.class, () -> authService.refreshToken("valid-but-missing"));
    }

    @Test
    void shouldLogoutSuccessfully() {
        authService.logout("some-refresh-token");

        verify(refreshTokenRepository).deleteByToken("some-refresh-token");
    }
}
