package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.application.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompleteAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentAuthorizationPort authorizationPort;

    private CompleteAppointmentUseCaseImpl completeAppointmentUseCase;

    private AppointmentId appointmentId;
    private Appointment existingAppointment;
    private PetId petId;
    private ClientId clientId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        completeAppointmentUseCase = new CompleteAppointmentUseCaseImpl(appointmentRepository, authorizationPort);
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
    void shouldCompleteAppointmentSuccessfully() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = completeAppointmentUseCase.execute(appointmentId);

        assertEquals(AppointmentStatus.COMPLETADA, result.getStatus());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldCompleteConfirmedAppointment() {
        Appointment confirmed = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.CONFIRMADA, null,
                petId, clientId, vetId, true);
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(confirmed));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = completeAppointmentUseCase.execute(appointmentId);

        assertEquals(AppointmentStatus.COMPLETADA, result.getStatus());
    }

    @Test
    void shouldCompleteInAttentionAppointment() {
        Appointment inAttention = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.EN_ATENCION, null,
                petId, clientId, vetId, true);
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(inAttention));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = completeAppointmentUseCase.execute(appointmentId);

        assertEquals(AppointmentStatus.COMPLETADA, result.getStatus());
    }

    @Test
    void shouldThrowNotFoundWhenAppointmentDoesNotExist() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                completeAppointmentUseCase.execute(appointmentId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenAlreadyCompleted() {
        Appointment completed = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.COMPLETADA, null,
                petId, clientId, vetId, true);
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(completed));

        assertThrows(BadRequestException.class, () ->
                completeAppointmentUseCase.execute(appointmentId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenCancelled() {
        Appointment cancelled = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.CANCELADA, "Client cancelled",
                petId, clientId, vetId, true);
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(cancelled));

        assertThrows(BadRequestException.class, () ->
                completeAppointmentUseCase.execute(appointmentId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowForbiddenWhenVetTriesToCompleteOtherVetAppointment() {
        UserId otherVetId = UserId.generate();
        Appointment otherVetAppointment = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.PENDIENTE, null,
                petId, clientId, otherVetId, true);

        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(otherVetAppointment));

        assertThrows(ForbiddenException.class, () ->
                completeAppointmentUseCase.execute(appointmentId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldAllowVetToCompleteOwnAppointment() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(existingAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = completeAppointmentUseCase.execute(appointmentId);

        assertEquals(AppointmentStatus.COMPLETADA, result.getStatus());
    }
}
