package com.medipet.medipet.clinical.infrastructure.outbound.persistence;

import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.clinical.infrastructure.outbound.persistence.entity.ConsultationEntity;
import com.medipet.medipet.clinical.infrastructure.outbound.persistence.mapper.ConsultationEntityMapper;
import com.medipet.medipet.clinical.infrastructure.outbound.persistence.repository.ConsultationJpaRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
class ConsultationPersistenceAdapter implements ConsultationRepository {

    private final ConsultationJpaRepository jpaRepository;

    ConsultationPersistenceAdapter(ConsultationJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Consultation save(Consultation consultation) {
        var entity = ConsultationEntityMapper.toEntity(consultation);
        var saved = jpaRepository.save(entity);
        return ConsultationEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Consultation> findById(ConsultationId id) {
        return jpaRepository.findById(id.getValue())
                .map(ConsultationEntityMapper::toDomain);
    }

    @Override
    public Optional<Consultation> findByAppointmentId(AppointmentId appointmentId) {
        return jpaRepository.findByAppointmentId(appointmentId.getValue())
                .map(ConsultationEntityMapper::toDomain);
    }

    @Override
    public Page<Consultation> findByPetId(PetId petId, Pageable pageable) {
        return jpaRepository.findByPetId(petId.getValue(), pageable)
                .map(ConsultationEntityMapper::toDomain);
    }
}
