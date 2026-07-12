package com.medipet.medipet.clinical.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Request to create a medical consultation")
public record CreateConsultationRequest(

        @Schema(description = "Appointment ID", example = "550e8400-e29b-41d4-a716-446655440000")
        @NotNull(message = "Appointment ID is required")
        UUID appointmentId,

        @Schema(description = "Pet ID", example = "550e8400-e29b-41d4-a716-446655440001")
        @NotNull(message = "Pet ID is required")
        UUID petId,

        @Schema(description = "Client ID", example = "550e8400-e29b-41d4-a716-446655440002")
        @NotNull(message = "Client ID is required")
        UUID clientId,

        @Schema(description = "Veterinarian ID", example = "550e8400-e29b-41d4-a716-446655440003")
        @NotNull(message = "Veterinarian ID is required")
        UUID veterinarianId,

        @Schema(description = "Weight in kg", example = "5.5")
        BigDecimal weight,

        @Schema(description = "Temperature in Celsius", example = "38.5")
        BigDecimal temperature,

        @Schema(description = "Symptoms", example = "Vomiting, diarrhea")
        String symptoms,

        @Schema(description = "Diagnosis", example = "Gastroenteritis")
        String diagnosis,

        @Schema(description = "Treatment", example = "Antibiotics and hydration therapy")
        String treatment,

        @Schema(description = "Additional notes", example = "Follow up in 2 weeks")
        String notes
) {}
