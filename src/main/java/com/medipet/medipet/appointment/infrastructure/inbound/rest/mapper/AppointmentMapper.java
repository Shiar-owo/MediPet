package com.medipet.medipet.appointment.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.appointment.application.AppointmentSummary;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.response.AppointmentResponse;

import java.time.format.DateTimeFormatter;

public final class AppointmentMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private AppointmentMapper() {
    }

    public static AppointmentResponse toResponse(AppointmentSummary summary) {
        return new AppointmentResponse(
                summary.getId().getValue().toString(),
                summary.getDate() != null ? summary.getDate().format(DATE_FORMAT) : null,
                summary.getStartTime() != null ? summary.getStartTime().format(TIME_FORMAT) : null,
                summary.getEndTime() != null ? summary.getEndTime().format(TIME_FORMAT) : null,
                summary.getReason(),
                summary.getStatus() != null ? summary.getStatus().name() : null,
                summary.getCancelReason(),
                summary.getPetId().getValue().toString(),
                summary.getPetName(),
                summary.getClientId().getValue().toString(),
                summary.getClientName(),
                summary.getVeterinarianId().getValue().toString(),
                summary.getVeterinarianName(),
                summary.isActive()
        );
    }
}
