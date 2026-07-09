package com.medipet.medipet.user.domain.port.outbound;

import com.medipet.medipet.user.domain.model.RefreshToken;

import java.util.Optional;

public interface RefreshTokenRepository {

    RefreshToken save(RefreshToken refreshToken);

    Optional<RefreshToken> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserEmail(String userEmail);
}
