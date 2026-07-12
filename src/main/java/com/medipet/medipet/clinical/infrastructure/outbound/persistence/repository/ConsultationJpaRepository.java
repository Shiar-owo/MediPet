package com.medipet.medipet.clinical.infrastructure.outbound.persistence.repository;

import com.medipet.medipet.clinical.infrastructure.outbound.persistence.entity.ConsultationEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ConsultationJpaRepository extends JpaRepository<ConsultationEntity, UUID> {

    Optional<ConsultationEntity> findByAppointmentId(UUID appointmentId);

    Page<ConsultationEntity> findByPetId(UUID petId, Pageable pageable);
}
