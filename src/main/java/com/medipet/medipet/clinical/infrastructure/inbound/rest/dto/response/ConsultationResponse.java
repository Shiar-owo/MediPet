package com.medipet.medipet.clinical.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Schema(description = "Consultation response")
public record ConsultationResponse(

        @Schema(description = "Consultation ID")
        UUID id,

        @Schema(description = "Appointment ID")
        UUID appointmentId,

        @Schema(description = "Pet ID")
        UUID petId,

        @Schema(description = "Pet name")
        String petName,

        @Schema(description = "Client ID")
        UUID clientId,

        @Schema(description = "Client name")
        String clientName,

        @Schema(description = "Veterinarian ID")
        UUID veterinarianId,

        @Schema(description = "Veterinarian name")
        String veterinarianName,

        @Schema(description = "Weight in kg")
        BigDecimal weight,

        @Schema(description = "Temperature in Celsius")
        BigDecimal temperature,

        @Schema(description = "Symptoms")
        String symptoms,

        @Schema(description = "Diagnosis")
        String diagnosis,

        @Schema(description = "Treatment")
        String treatment,

        @Schema(description = "Additional notes")
        String notes,

        @Schema(description = "Active status")
        boolean active,

        @Schema(description = "Created at")
        LocalDateTime createdAt,

        @Schema(description = "Updated at")
        LocalDateTime updatedAt
) {}
