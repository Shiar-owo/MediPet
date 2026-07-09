package com.medipet.medipet.appointment.domain.port.inbound;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;

import java.time.LocalDate;
import java.time.LocalTime;

public interface UpdateAppointmentUseCase {
    Appointment execute(AppointmentId id, LocalDate date, LocalTime startTime,
                        LocalTime endTime, String reason);
}
