package com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Appointment creation request")
public class AppointmentRequest {

    @NotBlank
    @Schema(description = "Appointment date (ISO-8601)", example = "2026-07-10")
    private String date;

    @NotBlank
    @Schema(description = "Start time (HH:mm)", example = "09:00")
    private String startTime;

    @NotBlank
    @Schema(description = "End time (HH:mm)", example = "10:00")
    private String endTime;

    @Schema(description = "Reason for visit", example = "Annual checkup")
    private String reason;

    @NotBlank
    @Schema(description = "Pet ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String petId;

    @NotBlank
    @Schema(description = "Client ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String clientId;

    @NotBlank
    @Schema(description = "Veterinarian user ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String veterinarianId;
}
