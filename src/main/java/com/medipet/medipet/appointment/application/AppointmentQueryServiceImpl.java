package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.port.inbound.AppointmentQueryService;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
class AppointmentQueryServiceImpl implements AppointmentQueryService {

    private final AppointmentRepository appointmentRepository;

    AppointmentQueryServiceImpl(AppointmentRepository appointmentRepository) {
        this.appointmentRepository = appointmentRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Appointment findById(AppointmentId id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Appointment", id.getValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByClientId(ClientId clientId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByClientId(clientId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByPetId(PetId petId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByPetId(petId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByVeterinarianId(UserId veterinarianId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianId(veterinarianId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByVeterinarianIdAndClientId(UserId veterinarianId, ClientId clientId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianIdAndClientId(veterinarianId, clientId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByVeterinarianIdAndPetId(UserId veterinarianId, PetId petId, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByVeterinarianIdAndPetId(veterinarianId, petId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findByDateRange(startDate, endDate, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Appointment> findAll(Pageable pageable) {
        Page<Appointment> page = appointmentRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
