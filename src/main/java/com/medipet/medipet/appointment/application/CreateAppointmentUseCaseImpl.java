package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.port.inbound.CreateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
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

    CreateAppointmentUseCaseImpl(AppointmentRepository appointmentRepository,
                                 PetRepository petRepository,
                                 ClientRepository clientRepository,
                                 UserRepository userRepository) {
        this.appointmentRepository = appointmentRepository;
        this.petRepository = petRepository;
        this.clientRepository = clientRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public Appointment execute(LocalDate date, LocalTime startTime, LocalTime endTime,
                               String reason, PetId petId, ClientId clientId,
                               UserId veterinarianId) {
        if (!petRepository.findById(petId).isPresent()) {
            throw new NotFoundException("Pet", petId.getValue());
        }

        if (!clientRepository.findById(clientId).isPresent()) {
            throw new NotFoundException("Client", clientId.getValue());
        }

        if (!userRepository.findById(veterinarianId).isPresent()) {
            throw new NotFoundException("Veterinarian", veterinarianId.getValue());
        }

        if (!endTime.isAfter(startTime)) {
            throw new BadRequestException("End time must be after start time");
        }

        var overlapping = appointmentRepository.findOverlapping(veterinarianId, date, startTime, endTime);
        if (!overlapping.isEmpty()) {
            throw new ConflictException("Appointment overlaps with an existing appointment for this veterinarian");
        }

        Appointment appointment = Appointment.create(date, startTime, endTime, reason,
                petId, clientId, veterinarianId);
        return appointmentRepository.save(appointment);
    }
}
