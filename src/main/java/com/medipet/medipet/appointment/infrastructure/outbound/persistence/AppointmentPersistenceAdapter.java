package com.medipet.medipet.appointment.infrastructure.outbound.persistence;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.appointment.infrastructure.outbound.persistence.mapper.AppointmentEntityMapper;
import com.medipet.medipet.appointment.infrastructure.outbound.persistence.repository.AppointmentJpaRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
class AppointmentPersistenceAdapter implements AppointmentRepository {

    private final AppointmentJpaRepository jpaRepository;

    AppointmentPersistenceAdapter(AppointmentJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Appointment save(Appointment appointment) {
        var entity = AppointmentEntityMapper.toEntity(appointment);
        var saved = jpaRepository.save(entity);
        return AppointmentEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Appointment> findById(AppointmentId id) {
        return jpaRepository.findById(id.getValue())
                .map(AppointmentEntityMapper::toDomain);
    }

    @Override
    public List<Appointment> findOverlapping(UserId veterinarianId, LocalDate date,
                                             LocalTime startTime, LocalTime endTime) {
        return jpaRepository.findOverlapping(veterinarianId.getValue(), date, startTime, endTime)
                .stream()
                .map(AppointmentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Page<Appointment> findByClientId(ClientId clientId, Pageable pageable) {
        return jpaRepository.findByClientId(clientId.getValue(), pageable)
                .map(AppointmentEntityMapper::toDomain);
    }

    @Override
    public Page<Appointment> findByPetId(PetId petId, Pageable pageable) {
        return jpaRepository.findByPetId(petId.getValue(), pageable)
                .map(AppointmentEntityMapper::toDomain);
    }

    @Override
    public Page<Appointment> findByVeterinarianId(UserId veterinarianId, Pageable pageable) {
        return jpaRepository.findByVeterinarianId(veterinarianId.getValue(), pageable)
                .map(AppointmentEntityMapper::toDomain);
    }

    @Override
    public Page<Appointment> findByDateRange(LocalDate startDate, LocalDate endDate, Pageable pageable) {
        return jpaRepository.findByDateBetween(startDate, endDate, pageable)
                .map(AppointmentEntityMapper::toDomain);
    }

    @Override
    public Page<Appointment> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(AppointmentEntityMapper::toDomain);
    }
}
