package com.medipet.medipet.appointment.infrastructure.outbound.persistence.mapper;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.appointment.infrastructure.outbound.persistence.entity.AppointmentEntity;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;

public class AppointmentEntityMapper {

    private AppointmentEntityMapper() {
    }

    public static AppointmentEntity toEntity(Appointment appointment) {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(appointment.getId().getValue());
        entity.setDate(appointment.getDate());
        entity.setStartTime(appointment.getStartTime());
        entity.setEndTime(appointment.getEndTime());
        entity.setReason(appointment.getReason());
        entity.setStatus(appointment.getStatus().name());
        entity.setCancelReason(appointment.getCancelReason());
        entity.setPetId(appointment.getPetId().getValue());
        entity.setClientId(appointment.getClientId().getValue());
        entity.setVeterinarianId(appointment.getVeterinarianId().getValue());
        entity.setActive(appointment.isActive());
        return entity;
    }

    public static Appointment toDomain(AppointmentEntity entity) {
        return Appointment.withId(
                AppointmentId.from(entity.getId()),
                entity.getDate(),
                entity.getStartTime(),
                entity.getEndTime(),
                entity.getReason(),
                AppointmentStatus.valueOf(entity.getStatus()),
                entity.getCancelReason(),
                PetId.from(entity.getPetId()),
                ClientId.from(entity.getClientId()),
                UserId.from(entity.getVeterinarianId()),
                entity.isActive()
        );
    }
}
