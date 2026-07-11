package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.domain.port.inbound.CancelAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CancelAppointmentUseCaseImpl implements CancelAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentAuthorizationPort authorizationPort;

    CancelAppointmentUseCaseImpl(AppointmentRepository appointmentRepository,
                                 AppointmentAuthorizationPort authorizationPort) {
        this.appointmentRepository = appointmentRepository;
        this.authorizationPort = authorizationPort;
    }

    @Override
    @Transactional
    public Appointment execute(AppointmentId id, String cancelReason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));

        enforceVeterinarianOwnership(appointment);

        if (appointment.getStatus() == AppointmentStatus.CANCELADA) {
            throw new BadRequestException("Appointment is already cancelled");
        }

        appointment.setStatus(AppointmentStatus.CANCELADA);
        appointment.setCancelReason(cancelReason);

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
