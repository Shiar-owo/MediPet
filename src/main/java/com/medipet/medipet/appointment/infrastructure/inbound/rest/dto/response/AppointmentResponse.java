package com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Appointment response")
public class AppointmentResponse {

    @Schema(description = "Appointment ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Appointment date", example = "2026-07-10")
    private String date;

    @Schema(description = "Start time", example = "09:00")
    private String startTime;

    @Schema(description = "End time", example = "10:00")
    private String endTime;

    @Schema(description = "Reason for visit", example = "Annual checkup")
    private String reason;

    @Schema(description = "Appointment status", example = "PENDIENTE")
    private String status;

    @Schema(description = "Cancellation reason", example = "Client requested")
    private String cancelReason;

    @Schema(description = "Pet ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String petId;

    @Schema(description = "Pet name", example = "Max")
    private String petName;

    @Schema(description = "Client ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String clientId;

    @Schema(description = "Client name", example = "Juan Pérez")
    private String clientName;

    @Schema(description = "Veterinarian user ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String veterinarianId;

    @Schema(description = "Veterinarian name", example = "Dr. García")
    private String veterinarianName;

    @Schema(description = "Whether the appointment is active", example = "true")
    private boolean active;
}
