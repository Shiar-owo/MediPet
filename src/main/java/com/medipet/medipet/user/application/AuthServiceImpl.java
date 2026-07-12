package com.medipet.medipet.user.application;

import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.shared.exception.UnauthorizedException;
import com.medipet.medipet.user.domain.model.RefreshToken;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.inbound.AuthService;
import com.medipet.medipet.user.domain.port.outbound.RefreshTokenRepository;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final TokenProvider tokenProvider;
    private final long refreshExpirationMs;

    AuthServiceImpl(UserRepository userRepository,
                    RefreshTokenRepository refreshTokenRepository,
                    TokenProvider tokenProvider,
                    @Value("${jwt.refresh-expiration}") long refreshExpirationMs) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.tokenProvider = tokenProvider;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    @Override
    @Transactional
    public LoginResult issueTokens(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        user.setLastLoginAt(LocalDateTime.now());
        userRepository.save(user);

        String accessToken = tokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String refreshTokenValue = tokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken refreshToken = RefreshToken.create(
                refreshTokenValue,
                user.getEmail(),
                LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000)
        );
        refreshTokenRepository.save(refreshToken);

        return new LoginResult(user.getId().getValue().toString(), user.getEmail(), accessToken, refreshTokenValue, user.getRole().name());
    }

    @Override
    @Transactional
    public LoginResult refreshToken(String refreshTokenValue) {
        if (!tokenProvider.validateToken(refreshTokenValue)) {
            throw new UnauthorizedException("Invalid refresh token");
        }

        RefreshToken storedToken = refreshTokenRepository.findByToken(refreshTokenValue)
                .orElseThrow(() -> new UnauthorizedException("Refresh token not found"));

        if (storedToken.isExpired()) {
            refreshTokenRepository.deleteByToken(refreshTokenValue);
            throw new UnauthorizedException("Refresh token expired");
        }

        String email = storedToken.getUserEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found"));

        refreshTokenRepository.deleteByToken(refreshTokenValue);

        String newAccessToken = tokenProvider.generateAccessToken(user.getEmail(), user.getRole().name());
        String newRefreshTokenValue = tokenProvider.generateRefreshToken(user.getEmail());

        RefreshToken newRefreshToken = RefreshToken.create(
                newRefreshTokenValue,
                user.getEmail(),
                LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000)
        );
        refreshTokenRepository.save(newRefreshToken);

        return new LoginResult(user.getId().getValue().toString(), user.getEmail(), newAccessToken, newRefreshTokenValue, user.getRole().name());
    }

    @Override
    @Transactional
    public void logout(String refreshTokenValue) {
        refreshTokenRepository.deleteByToken(refreshTokenValue);
    }
}
