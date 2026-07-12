package com.medipet.medipet.clinical.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class ConsultationId {

    private final UUID value;

    private ConsultationId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ConsultationId value cannot be null");
        }
        this.value = value;
    }

    public static ConsultationId generate() {
        return new ConsultationId(UUID.randomUUID());
    }

    public static ConsultationId from(UUID value) {
        return new ConsultationId(value);
    }

    public static ConsultationId from(String value) {
        return new ConsultationId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConsultationId that = (ConsultationId) o;
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
