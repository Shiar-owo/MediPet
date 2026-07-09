package com.medipet.medipet.pet.infrastructure.outbound.persistence.repository;

import com.medipet.medipet.pet.infrastructure.outbound.persistence.entity.PetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PetJpaRepository extends JpaRepository<PetEntity, UUID> {

    Optional<PetEntity> findByMicrochipId(String microchipId);

    Optional<PetEntity> findByTattooNumber(String tattooNumber);

    Page<PetEntity> findByClientId(UUID clientId, Pageable pageable);

    Page<PetEntity> findByNameContainingIgnoreCase(String name, Pageable pageable);

    boolean existsByMicrochipId(String microchipId);

    boolean existsByTattooNumber(String tattooNumber);
}
