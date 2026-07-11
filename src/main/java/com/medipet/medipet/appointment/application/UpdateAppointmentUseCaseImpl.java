package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.domain.port.inbound.UpdateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
class UpdateAppointmentUseCaseImpl implements UpdateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentAuthorizationPort authorizationPort;

    UpdateAppointmentUseCaseImpl(AppointmentRepository appointmentRepository,
                                 AppointmentAuthorizationPort authorizationPort) {
        this.appointmentRepository = appointmentRepository;
        this.authorizationPort = authorizationPort;
    }

    @Override
    @Transactional
    public Appointment execute(AppointmentId id, LocalDate date, LocalTime startTime,
                               LocalTime endTime, String reason) {
        Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));

        enforceVeterinarianOwnership(appointment);

        LocalDate newDate = date != null ? date : appointment.getDate();
        LocalTime newStartTime = startTime != null ? startTime : appointment.getStartTime();
        LocalTime newEndTime = endTime != null ? endTime : appointment.getEndTime();

        if (!newEndTime.isAfter(newStartTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        boolean timeChanged = date != null || startTime != null || endTime != null;
        if (timeChanged) {
            var overlapping = appointmentRepository.findOverlapping(
                    appointment.getVeterinarianId(), newDate, newStartTime, newEndTime);
            for (Appointment existing : overlapping) {
                if (!existing.getId().equals(id)) {
                    throw new ConflictException("Appointment overlaps with an existing appointment for this veterinarian");
                }
            }
        }

        if (date != null) appointment.setDate(date);
        if (startTime != null) appointment.setStartTime(startTime);
        if (endTime != null) appointment.setEndTime(endTime);
        if (reason != null) appointment.setReason(reason);

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
