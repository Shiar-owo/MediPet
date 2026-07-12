package com.medipet.medipet.appointment.infrastructure.outbound.persistence.entity;

import com.medipet.medipet.shared.domain.Auditable;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@Entity
@Table(name = "appointments")
public class AppointmentEntity extends Auditable {

    @Id
    @Column(columnDefinition = "UUID")
    private UUID id;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(length = 500)
    private String reason;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "cancel_reason", length = 500)
    private String cancelReason;

    @Column(name = "pet_id", nullable = false, columnDefinition = "UUID")
    private UUID petId;

    @Column(name = "client_id", nullable = false, columnDefinition = "UUID")
    private UUID clientId;

    @Column(name = "veterinarian_id", nullable = false, columnDefinition = "UUID")
    private UUID veterinarianId;

    @Column(nullable = false)
    private boolean active;
}
