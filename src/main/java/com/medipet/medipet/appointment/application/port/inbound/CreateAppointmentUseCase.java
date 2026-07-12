package com.medipet.medipet.appointment.application.port.inbound;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

import java.time.LocalDate;
import java.time.LocalTime;

public interface CreateAppointmentUseCase {
    Appointment execute(LocalDate date, LocalTime startTime, LocalTime endTime,
                        String reason, PetId petId, ClientId clientId,
                        UserId veterinarianId);
}
