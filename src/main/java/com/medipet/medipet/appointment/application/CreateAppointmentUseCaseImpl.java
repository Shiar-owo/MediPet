package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.application.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.application.port.inbound.CreateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;

@Service
class CreateAppointmentUseCaseImpl implements CreateAppointmentUseCase {

    private final AppointmentRepository appointmentRepository;
    private final PetRepository petRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final AppointmentAuthorizationPort authorizationPort;

    CreateAppointmentUseCaseImpl(AppointmentRepository appointmentRepository,
                                 PetRepository petRepository,
                                 ClientRepository clientRepository,
                                 UserRepository userRepository,
                                 AppointmentAuthorizationPort authorizationPort) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
        this.authorizationPort = authorizationPort;
    }

    @Override
    @Transactional
    public Appointment execute(LocalDate date, LocalTime startTime, LocalTime endTime,
                               String reason, PetId petId, ClientId clientId,
                               UserId veterinarianId) {
        UserId resolvedVetId = resolveVetId(veterinarianId);

        if (!petRepository.findById(petId).isPresent()) {
            throw new NotFoundException("Pet", petId.getValue());
        }

        if (!clientRepository.findById(clientId).isPresent()) {
            throw new NotFoundException("Client", clientId.getValue());
        }

        if (!userRepository.findById(resolvedVetId).isPresent()) {
            throw new NotFoundException("Veterinarian", resolvedVetId.getValue());
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        var overlapping = appointmentRepository.findOverlapping(resolvedVetId, date, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new ConflictException("Appointment overlaps with an existing appointment for this veterinarian");
        }

        Appointment appointment = Appointment.create(date, startTime, endTime, reason,
                petId, clientId, resolvedVetId);
        return appointmentRepository.save(appointment);
    }

    private UserId resolveVetId(UserId requestedVetId) {
        if (authorizationPort.getCurrentUserRole() == Role.VETERINARIO) {
            UserId currentUserId = authorizationPort.getCurrentUserId();
            if (!currentUserId.equals(requestedVetId)) {
                throw new ForbiddenException("Veterinarians can only create appointments for themselves");
            }
            return currentUserId;
        }
        return requestedVetId;
    }
}
