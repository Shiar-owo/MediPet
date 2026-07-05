package com.medipet.medipet.user.adapter.outbound.persistence.repository;

import com.medipet.medipet.user.adapter.outbound.persistence.entity.RefreshTokenEntity;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshTokenEntity, UUID> {

    Optional<RefreshTokenEntity> findByToken(String token);

    void deleteByToken(String token);

    void deleteByUserEmail(String userEmail);
}
