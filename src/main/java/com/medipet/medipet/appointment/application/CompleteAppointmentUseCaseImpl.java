package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.application.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.application.port.inbound.CompleteAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CompleteAppointmentUseCaseImpl implements CompleteAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentAuthorizationPort authorizationPort;

    CompleteAppointmentUseCaseImpl(AppointmentRepository appointmentRepository,
                                   AppointmentAuthorizationPort authorizationPort) {
        this.appointmentRepository = appointmentRepository;
        this.authorizationPort = authorizationPort;
    }

    @Override
    @Transactional
    public Appointment execute(AppointmentId id) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));

        enforceVeterinarianOwnership(appointment);

        if (appointment.getStatus() == AppointmentStatus.COMPLETADA) {
            throw new BadRequestException("Appointment is already completed");
        }

        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new BadRequestException("Cannot complete a cancelled appointment");
        }

        appointment.setStatus(AppointmentStatus.COMPLETADA);

        return appointmentRepository.save(appointment);
    }

    private void enforceVeterinarianOwnership(Appointment appointment) {
        if (authorizationPort.getCurrentUserRole() == Role.VETERINARIO) {
            if (!appointment.getVeterinarianId().equals(authorizationPort.getCurrentUserId())) {
                throw new ForbiddenException("Veterinarians can only access their own appointments");
            }
        }
    }
}
