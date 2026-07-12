package com.medipet.medipet.appointment.infrastructure.outbound.persistence.mapper;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.appointment.infrastructure.outbound.persistence.entity.AppointmentEntity;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class AppointmentEntityMapperTest {

    private final AppointmentId appointmentId = AppointmentId.from(UUID.fromString("a1b2c3d4-e5f6-7890-abcd-ef1234567890"));
    private final PetId petId = PetId.from(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901"));
    private final ClientId clientId = ClientId.from(UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012"));
    private final UserId vetId = UserId.from(UUID.fromString("d4e5f6a7-b8c9-0123-defa-234567890123"));

    @Test
    void shouldConvertDomainToEntity() {
        Appointment appointment = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.CONFIRMADA, "Client cancelled",
                petId, clientId, vetId, true);

        AppointmentEntity entity = AppointmentEntityMapper.toEntity(appointment);

        assertEquals(appointmentId.getValue(), entity.getId());
        assertEquals(LocalDate.of(2026, 7, 10), entity.getDate());
        assertEquals(LocalTime.of(9, 0), entity.getStartTime());
        assertEquals(LocalTime.of(10, 0), entity.getEndTime());
        assertEquals("Annual checkup", entity.getReason());
        assertEquals("CONFIRMADA", entity.getStatus());
        assertEquals("Client cancelled", entity.getCancelReason());
        assertEquals(petId.getValue(), entity.getPetId());
        assertEquals(clientId.getValue(), entity.getClientId());
        assertEquals(vetId.getValue(), entity.getVeterinarianId());
        assertTrue(entity.isActive());
    }

    @Test
    void shouldConvertEntityToDomain() {
        AppointmentEntity entity = new AppointmentEntity();
        entity.setId(appointmentId.getValue());
        entity.setDate(LocalDate.of(2026, 7, 10));
        entity.setStartTime(LocalTime.of(9, 0));
        entity.setEndTime(LocalTime.of(10, 0));
        entity.setReason("Annual checkup");
        entity.setStatus("PENDIENTE");
        entity.setCancelReason(null);
        entity.setPetId(petId.getValue());
        entity.setClientId(clientId.getValue());
        entity.setVeterinarianId(vetId.getValue());
        entity.setActive(true);

        Appointment appointment = AppointmentEntityMapper.toDomain(entity);

        assertEquals(appointmentId, appointment.getId());
        assertEquals(LocalDate.of(2026, 7, 10), appointment.getDate());
        assertEquals(LocalTime.of(9, 0), appointment.getStartTime());
        assertEquals(LocalTime.of(10, 0), appointment.getEndTime());
        assertEquals("Annual checkup", appointment.getReason());
        assertEquals(AppointmentStatus.PENDIENTE, appointment.getStatus());
        assertNull(appointment.getCancelReason());
        assertEquals(petId, appointment.getPetId());
        assertEquals(clientId, appointment.getClientId());
        assertEquals(vetId, appointment.getVeterinarianId());
        assertTrue(appointment.isActive());
    }

    @Test
    void shouldRoundTripDomainToEntityToDomain() {
        Appointment original = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(14, 30), LocalTime.of(15, 30),
                "Vaccination", AppointmentStatus.EN_ATENCION, null,
                petId, clientId, vetId, true);

        AppointmentEntity entity = AppointmentEntityMapper.toEntity(original);
        Appointment result = AppointmentEntityMapper.toDomain(entity);

        assertEquals(original.getId(), result.getId());
        assertEquals(original.getDate(), result.getDate());
        assertEquals(original.getStartTime(), result.getStartTime());
        assertEquals(original.getEndTime(), result.getEndTime());
        assertEquals(original.getReason(), result.getReason());
        assertEquals(original.getStatus(), result.getStatus());
        assertEquals(original.getCancelReason(), result.getCancelReason());
        assertEquals(original.getPetId(), result.getPetId());
        assertEquals(original.getClientId(), result.getClientId());
        assertEquals(original.getVeterinarianId(), result.getVeterinarianId());
        assertEquals(original.isActive(), result.isActive());
    }

    @Test
    void shouldHandleAllStatuses() {
        for (AppointmentStatus status : AppointmentStatus.values()) {
            Appointment appointment = Appointment.withId(appointmentId,
                    LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                    "Test", status, null, petId, clientId, vetId, true);

            AppointmentEntity entity = AppointmentEntityMapper.toEntity(appointment);
            Appointment result = AppointmentEntityMapper.toDomain(entity);

            assertEquals(status, result.getStatus());
            assertEquals(status.name(), entity.getStatus());
        }
    }
}
