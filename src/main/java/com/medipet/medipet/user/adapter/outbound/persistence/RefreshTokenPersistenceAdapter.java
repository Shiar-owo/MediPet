package com.medipet.medipet.user.adapter.outbound.persistence;

import com.medipet.medipet.user.adapter.outbound.persistence.entity.RefreshTokenEntity;
import com.medipet.medipet.user.adapter.outbound.persistence.repository.RefreshTokenJpaRepository;
import com.medipet.medipet.user.domain.model.RefreshToken;
import com.medipet.medipet.user.domain.port.outbound.RefreshTokenRepository;
import com.medipet.medipet.user.domain.valueobject.RefreshTokenId;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class RefreshTokenPersistenceAdapter implements RefreshTokenRepository {

    private final RefreshTokenJpaRepository jpaRepository;

    RefreshTokenPersistenceAdapter(RefreshTokenJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RefreshToken save(RefreshToken refreshToken) {
        RefreshTokenEntity entity = toEntity(refreshToken);
        RefreshTokenEntity saved = jpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<RefreshToken> findByToken(String token) {
        return jpaRepository.findByToken(token)
                .map(this::toDomain);
    }

    @Override
    public void deleteByToken(String token) {
        jpaRepository.deleteByToken(token);
    }

    @Override
    public void deleteByUserEmail(String userEmail) {
        jpaRepository.deleteByUserEmail(userEmail);
    }

    private RefreshTokenEntity toEntity(RefreshToken refreshToken) {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setId(refreshToken.getId().getValue());
        entity.setToken(refreshToken.getToken());
        entity.setUserEmail(refreshToken.getUserEmail());
        entity.setExpiresAt(refreshToken.getExpiresAt());
        return entity;
    }

    private RefreshToken toDomain(RefreshTokenEntity entity) {
        return RefreshToken.withId(
                RefreshTokenId.from(entity.getId()),
                entity.getToken(),
                entity.getUserEmail(),
                entity.getExpiresAt()
        );
    }
}
