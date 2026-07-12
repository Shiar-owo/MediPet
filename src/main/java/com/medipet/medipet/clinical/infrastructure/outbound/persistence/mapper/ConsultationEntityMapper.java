package com.medipet.medipet.clinical.infrastructure.outbound.persistence.mapper;

import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.clinical.infrastructure.outbound.persistence.entity.ConsultationEntity;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

public class ConsultationEntityMapper {

    private ConsultationEntityMapper() {}

    public static ConsultationEntity toEntity(Consultation consultation) {
        var entity = new ConsultationEntity();
        entity.setId(consultation.getId().getValue());
        entity.setAppointmentId(consultation.getAppointmentId().getValue());
        entity.setPetId(consultation.getPetId().getValue());
        entity.setClientId(consultation.getClientId().getValue());
        entity.setVeterinarianId(consultation.getVeterinarianId().getValue());
        entity.setWeight(consultation.getWeight());
        entity.setTemperature(consultation.getTemperature());
        entity.setSymptoms(consultation.getSymptoms());
        entity.setDiagnosis(consultation.getDiagnosis());
        entity.setTreatment(consultation.getTreatment());
        entity.setNotes(consultation.getNotes());
        entity.setActive(consultation.isActive());
        return entity;
    }

    public static Consultation toDomain(ConsultationEntity entity) {
        return Consultation.withId(
                ConsultationId.from(entity.getId()),
                AppointmentId.from(entity.getAppointmentId()),
                PetId.from(entity.getPetId()),
                ClientId.from(entity.getClientId()),
                UserId.from(entity.getVeterinarianId()),
                entity.getWeight(),
                entity.getTemperature(),
                entity.getSymptoms(),
                entity.getDiagnosis(),
                entity.getTreatment(),
                entity.getNotes(),
                entity.isActive()
        );
    }
}
