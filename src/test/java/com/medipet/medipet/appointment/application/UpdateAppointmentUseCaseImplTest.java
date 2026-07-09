package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    private UpdateAppointmentUseCaseImpl updateAppointmentUseCase;

    private AppointmentId appointmentId;
    private Appointment existingAppointment;
    private PetId petId;
    private ClientId clientId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        updateAppointmentUseCase = new UpdateAppointmentUseCaseImpl(appointmentRepository);
        appointmentId = AppointmentId.generate();
        petId = PetId.generate();
        clientId = ClientId.generate();
        vetId = UserId.generate();
        existingAppointment = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.PENDIENTE, null,
                petId, clientId, vetId, true);
    }

    @Test
    void shouldUpdateAppointmentSuccessfully() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.findOverlapping(vetId, LocalDate.of(2026, 7, 11),
                LocalTime.of(14, 0), LocalTime.of(15, 0))).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = updateAppointmentUseCase.execute(appointmentId,
                LocalDate.of(2026, 7, 11), LocalTime.of(14, 0), LocalTime.of(15, 0),
                "Updated reason");

        assertEquals(LocalDate.of(2026, 7, 11), result.getDate());
        assertEquals(LocalTime.of(14, 0), result.getStartTime());
        assertEquals(LocalTime.of(15, 0), result.getEndTime());
        assertEquals("Updated reason", result.getReason());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowNotFoundWhenAppointmentDoesNotExist() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                updateAppointmentUseCase.execute(appointmentId,
                        LocalDate.of(2026, 7, 11), LocalTime.of(14, 0), LocalTime.of(15, 0),
                        "Updated reason"));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictWhenOverlapOnTimeChange() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        Appointment overlapping = Appointment.create(LocalDate.of(2026, 7, 11),
                LocalTime.of(14, 30), LocalTime.of(15, 30), "Other", petId, clientId, vetId);
        when(appointmentRepository.findOverlapping(vetId, LocalDate.of(2026, 7, 11),
                LocalTime.of(14, 0), LocalTime.of(15, 0))).thenReturn(List.of(overlapping));

        assertThrows(ConflictException.class, () ->
                updateAppointmentUseCase.execute(appointmentId,
                        LocalDate.of(2026, 7, 11), LocalTime.of(14, 0), LocalTime.of(15, 0),
                        "Updated reason"));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldNotCheckOverlapWhenTimeNotChanged() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = updateAppointmentUseCase.execute(appointmentId,
                null, null, null, "Updated reason only");

        assertEquals("Updated reason only", result.getReason());
        assertEquals(LocalDate.of(2026, 7, 10), result.getDate());
        verify(appointmentRepository, never()).findOverlapping(any(), any(), any(), any());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowBadRequestWhenEndTimeBeforeStartTime() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));

        assertThrows(BadRequestException.class, () ->
                updateAppointmentUseCase.execute(appointmentId,
                        LocalDate.of(2026, 7, 11), LocalTime.of(15, 0), LocalTime.of(14, 0),
                        "Bad times"));

        verify(appointmentRepository, never()).save(any());
    }
}
