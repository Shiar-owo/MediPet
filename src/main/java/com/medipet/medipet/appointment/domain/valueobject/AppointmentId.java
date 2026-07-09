package com.medipet.medipet.appointment.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class AppointmentId {
    private final UUID value;

    private AppointmentId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("AppointmentId value cannot be null");
        }
        this.value = value;
    }

    public static AppointmentId generate() {
        return new AppointmentId(UUID.randomUUID());
    }

    public static AppointmentId from(UUID value) {
        return new AppointmentId(value);
    }

    public static AppointmentId from(String value) {
        return new AppointmentId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AppointmentId that = (AppointmentId) o;
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
