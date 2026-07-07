package com.medipet.medipet.client.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

public class ClientId {

    private final UUID value;

    private ClientId(UUID value) {
        if (value == null) {
            throw new IllegalArgumentException("ClientId value cannot be null");
        }
        this.value = value;
    }

    public static ClientId generate() {
        return new ClientId(UUID.randomUUID());
    }

    public static ClientId from(UUID value) {
        return new ClientId(value);
    }

    public static ClientId from(String value) {
        return new ClientId(UUID.fromString(value));
    }

    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientId clientId = (ClientId) o;
        return Objects.equals(value, clientId.value);
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
