package com.medipet.medipet.user.infrastructure.outbound.security;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BcryptPasswordAdapterTest {

    private BcryptPasswordAdapter passwordAdapter;

    @BeforeEach
    void setUp() {
        passwordAdapter = new BcryptPasswordAdapter();
    }

    @Test
    void shouldEncodePassword() {
        String encoded = passwordAdapter.encode("password123");

        assertNotNull(encoded);
        assertNotEquals("password123", encoded);
        assertTrue(encoded.startsWith("$2a$") || encoded.startsWith("$2b$"));
    }

    @Test
    void shouldMatchRawPasswordWithEncoded() {
        String encoded = passwordAdapter.encode("password123");

        assertTrue(passwordAdapter.matches("password123", encoded));
    }

    @Test
    void shouldNotMatchWrongPassword() {
        String encoded = passwordAdapter.encode("password123");

        assertFalse(passwordAdapter.matches("wrong_password", encoded));
    }

    @Test
    void shouldGenerateDifferentHashesForSamePassword() {
        String hash1 = passwordAdapter.encode("password123");
        String hash2 = passwordAdapter.encode("password123");

        assertNotEquals(hash1, hash2);
        assertTrue(passwordAdapter.matches("password123", hash1));
        assertTrue(passwordAdapter.matches("password123", hash2));
    }
}
