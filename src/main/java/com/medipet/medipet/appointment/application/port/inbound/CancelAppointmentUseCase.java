package com.medipet.medipet.appointment.application.port.inbound;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;

public interface CancelAppointmentUseCase {
    Appointment execute(AppointmentId id, String cancelReason);
}
