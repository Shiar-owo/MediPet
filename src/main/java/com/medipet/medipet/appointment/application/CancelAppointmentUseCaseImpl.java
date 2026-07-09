package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.inbound.CancelAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CancelAppointmentUseCaseImpl implements CancelAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;

    CancelAppointmentUseCaseImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional
    public Appointment execute(AppointmentId id, String cancelReason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));

        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELADA);
        appointment.setCancelReason(cancelReason);

        return appointmentRepository.save(appointment);
    }
}
