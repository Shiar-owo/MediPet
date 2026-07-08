package com.medipet.medipet.client.infrastructure.outbound.persistence.repository;

import com.medipet.medipet.client.infrastructure.outbound.persistence.entity.ClientEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ClientJpaRepository extends JpaRepository<ClientEntity, UUID> {

    Optional<ClientEntity> findByDni(String dni);

    boolean existsByDni(String dni);

    Page<ClientEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
