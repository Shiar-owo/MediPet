package com.medipet.medipet.appointment.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.infrastructure.inbound.rest.dto.response.AppointmentResponse;

import java.time.format.DateTimeFormatter;

public final class AppointmentMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private AppointmentMapper() {
    }

    public static AppointmentResponse toResponse(Appointment appointment) {
        return new AppointmentResponse(
                appointment.getId().getValue().toString(),
                appointment.getDate() != null ? appointment.getDate().format(DATE_FORMAT) : null,
                appointment.getStartTime() != null ? appointment.getStartTime().format(TIME_FORMAT) : null,
                appointment.getEndTime() != null ? appointment.getEndTime().format(TIME_FORMAT) : null,
                appointment.getReason(),
                appointment.getStatus() != null ? appointment.getStatus().name() : null,
                appointment.getCancelReason(),
                appointment.getPetId().getValue().toString(),
                appointment.getClientId().getValue().toString(),
                appointment.getVeterinarianId().getValue().toString(),
                appointment.isActive()
        );
    }
}
