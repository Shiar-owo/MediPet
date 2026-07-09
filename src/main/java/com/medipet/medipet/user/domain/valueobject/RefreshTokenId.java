package com.medipet.medipet.user.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class RefreshTokenId {

    private final UUID value;

    private RefreshTokenId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("RefreshTokenId value cannot be null");
        }
        this.value = value;
    }

    public static RefreshTokenId generate() {
        return new RefreshTokenId(UUID.randomUUID());
    }

    public static RefreshTokenId from(UUID value) {
        return new RefreshTokenId(value);
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RefreshTokenId that = (RefreshTokenId) o;
        return Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
