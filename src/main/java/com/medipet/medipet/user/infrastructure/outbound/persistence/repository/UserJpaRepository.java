package com.medipet.medipet.user.infrastructure.outbound.persistence.repository;

import com.medipet.medipet.user.infrastructure.outbound.persistence.entity.UserEntity;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserJpaRepository extends JpaRepository<UserEntity, UUID> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    Page<UserEntity> findByRole(String role, Pageable pageable);
}
