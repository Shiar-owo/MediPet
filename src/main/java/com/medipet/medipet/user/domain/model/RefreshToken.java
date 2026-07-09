package com.medipet.medipet.user.domain.model;

import com.medipet.medipet.user.domain.valueobject.RefreshTokenId;

import java.time.LocalDateTime;
import java.util.Objects;

public class RefreshToken {

    private RefreshTokenId id;
    private String token;
    private String userEmail;
    private LocalDateTime expiresAt;

    private RefreshToken() {
    }

    private RefreshToken(RefreshTokenId id, String token, String userEmail, LocalDateTime expiresAt) {
        this.id = id;
        this.token = token;
        this.userEmail = userEmail;
        this.expiresAt = expiresAt;
    }

    public static RefreshToken create(String token, String userEmail, LocalDateTime expiresAt) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.id = RefreshTokenId.generate();
        refreshToken.token = token;
        refreshToken.userEmail = userEmail;
        refreshToken.expiresAt = expiresAt;
        return refreshToken;
    }

    public static RefreshToken withId(RefreshTokenId id, String token, String userEmail, LocalDateTime expiresAt) {
        return new RefreshToken(id, token, userEmail, expiresAt);
    }

    public RefreshTokenId getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshToken that = (RefreshToken) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
