package com.medipet.medipet.clinical.infrastructure.outbound.persistence.entity;

import com.medipet.medipet.shared.domain.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "consultations")
public class ConsultationEntity extends Auditable {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(name = "appointment_id", nullable = false, columnDefinition = "UUID")
    private UUID appointmentId;

    @Column(name = "pet_id", nullable = false, columnDefinition = "UUID")
    private UUID petId;

    @Column(name = "client_id", nullable = false, columnDefinition = "UUID")
    private UUID clientId;

    @Column(name = "veterinarian_id", nullable = false, columnDefinition = "UUID")
    private UUID veterinarianId;

    @Column(precision = 6, scale = 2)
    private BigDecimal weight;

    @Column(precision = 4, scale = 1)
    private BigDecimal temperature;

    @Column(columnDefinition = "TEXT")
    private String symptoms;

    @Column(columnDefinition = "TEXT")
    private String diagnosis;

    @Column(columnDefinition = "TEXT")
    private String treatment;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private boolean active;
}
