package com.medipet.medipet.clinical.application;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record ConsultationSummary(
        UUID id,
        UUID appointmentId,
        UUID petId,
        String petName,
        UUID clientId,
        String clientName,
        UUID veterinarianId,
        String veterinarianName,
        BigDecimal weight,
        BigDecimal temperature,
        String symptoms,
        String diagnosis,
        String treatment,
        String notes,
        boolean active,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
