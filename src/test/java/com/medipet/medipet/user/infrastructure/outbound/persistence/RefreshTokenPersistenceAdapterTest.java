package com.medipet.medipet.user.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.user.domain.model.RefreshToken;
import com.medipet.medipet.user.domain.port.outbound.RefreshTokenRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class RefreshTokenPersistenceAdapterTest {

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Test
    void shouldSaveAndFindByToken() {
        RefreshToken token = RefreshToken.create("test-refresh-token",
                "user@example.com", LocalDateTime.now().plusDays(7));

        refreshTokenRepository.save(token);

        Optional<RefreshToken> found = refreshTokenRepository.findByToken("test-refresh-token");

        assertTrue(found.isPresent());
        assertEquals("user@example.com", found.get().getUserEmail());
        assertFalse(found.get().isExpired());
    }

    @Test
    void shouldReturnEmptyForNonexistentToken() {
        Optional<RefreshToken> found = refreshTokenRepository.findByToken("nonexistent");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldDeleteByToken() {
        RefreshToken token = RefreshToken.create("token-to-delete",
                "user@example.com", LocalDateTime.now().plusDays(7));
        refreshTokenRepository.save(token);

        refreshTokenRepository.deleteByToken("token-to-delete");

        assertTrue(refreshTokenRepository.findByToken("token-to-delete").isEmpty());
    }

    @Test
    void shouldDeleteByUserEmail() {
        refreshTokenRepository.save(RefreshToken.create("token1", "user@example.com",
                LocalDateTime.now().plusDays(7)));
        refreshTokenRepository.save(RefreshToken.create("token2", "user@example.com",
                LocalDateTime.now().plusDays(7)));
        refreshTokenRepository.save(RefreshToken.create("token3", "other@example.com",
                LocalDateTime.now().plusDays(7)));

        refreshTokenRepository.deleteByUserEmail("user@example.com");

        assertTrue(refreshTokenRepository.findByToken("token1").isEmpty());
        assertTrue(refreshTokenRepository.findByToken("token2").isEmpty());
        assertFalse(refreshTokenRepository.findByToken("token3").isEmpty());
    }

    @Test
    void shouldDetectExpiredToken() {
        RefreshToken expired = RefreshToken.create("expired-token",
                "user@example.com", LocalDateTime.now().minusDays(1));

        assertTrue(expired.isExpired());
    }

    @Test
    void shouldDetectNonExpiredToken() {
        RefreshToken valid = RefreshToken.create("valid-token",
                "user@example.com", LocalDateTime.now().plusDays(7));

        assertFalse(valid.isExpired());
    }
}
