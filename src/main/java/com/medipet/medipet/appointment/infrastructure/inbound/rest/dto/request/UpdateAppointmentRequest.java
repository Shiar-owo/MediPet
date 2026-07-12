package com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Update appointment request (all fields optional)")
public class UpdateAppointmentRequest {

    @Schema(description = "Appointment date (ISO-8601)", example = "2026-07-11")
    private String date;

    @Schema(description = "Start time (HH:mm)", example = "14:00")
    private String startTime;

    @Schema(description = "End time (HH:mm)", example = "15:00")
    private String endTime;

    @Schema(description = "Reason for visit", example = "Vaccination")
    private String reason;
}
