package com.medipet.medipet.appointment.infrastructure.outbound.persistence.repository;

import com.medipet.medipet.appointment.infrastructure.outbound.persistence.entity.AppointmentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentJpaRepository extends JpaRepository<AppointmentEntity, UUID> {

    @Query("""
            SELECT a FROM AppointmentEntity a
            WHERE a.veterinarianId = :veterinarianId
            AND a.date = :date
            AND a.status <> 'CANCELADA'
            AND a.startTime < :endTime
            AND a.endTime > :startTime
            """)
    List<AppointmentEntity> findOverlapping(
            @Param("veterinarianId") UUID veterinarianId,
            @Param("date") LocalDate date,
            @Param("startTime") LocalTime startTime,
            @Param("endTime") LocalTime endTime);

    Page<AppointmentEntity> findByClientId(UUID clientId, Pageable pageable);

    Page<AppointmentEntity> findByPetId(UUID petId, Pageable pageable);

    Page<AppointmentEntity> findByVeterinarianId(UUID veterinarianId, Pageable pageable);

    Page<AppointmentEntity> findByVeterinarianIdAndClientId(UUID veterinarianId, UUID clientId, Pageable pageable);

    Page<AppointmentEntity> findByVeterinarianIdAndPetId(UUID veterinarianId, UUID petId, Pageable pageable);

    Page<AppointmentEntity> findByDateBetween(LocalDate startDate, LocalDate endDate, Pageable pageable);
}
