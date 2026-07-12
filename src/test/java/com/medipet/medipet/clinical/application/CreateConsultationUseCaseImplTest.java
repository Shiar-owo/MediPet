package com.medipet.medipet.clinical.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.application.port.inbound.CreateConsultationUseCase;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateConsultationUseCaseImplTest {

    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private AppointmentRepository appointmentRepository;

    private CreateConsultationUseCaseImpl createConsultationUseCase;

    private UUID appointmentId;
    private UUID petId;
    private UUID clientId;
    private UUID vetId;

    @BeforeEach
    void setUp() {
        createConsultationUseCase = new CreateConsultationUseCaseImpl(
                consultationRepository, appointmentRepository);
        appointmentId = UUID.randomUUID();
        petId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        vetId = UUID.randomUUID();
    }

    @Test
    void shouldCreateConsultationSuccessfully() {
        Appointment appointment = Appointment.withId(
                AppointmentId.from(appointmentId),
                LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Checkup",
                AppointmentStatus.COMPLETADA,
                null,
                PetId.from(petId),
                ClientId.from(clientId),
                UserId.from(vetId),
                true);
        when(appointmentRepository.findById(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.of(appointment));
        when(consultationRepository.findByAppointmentId(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.empty());
        when(consultationRepository.save(any(Consultation.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        CreateConsultationUseCase.Command command = new CreateConsultationUseCase.Command(
                appointmentId, petId, clientId, vetId,
                new BigDecimal("5.5"), new BigDecimal("38.5"),
                "Vomiting", "Gastroenteritis", "Antibiotics", "Follow up");

        Consultation result = createConsultationUseCase.execute(command);

        assertNotNull(result);
        assertEquals(appointmentId, result.getAppointmentId().getValue());
        assertEquals(petId, result.getPetId().getValue());
        assertEquals(clientId, result.getClientId().getValue());
        assertEquals(vetId, result.getVeterinarianId().getValue());
        assertEquals(new BigDecimal("5.5"), result.getWeight());
        assertEquals(new BigDecimal("38.5"), result.getTemperature());
        assertEquals("Vomiting", result.getSymptoms());
        assertEquals("Gastroenteritis", result.getDiagnosis());
        assertEquals("Antibiotics", result.getTreatment());
        assertEquals("Follow up", result.getNotes());
        verify(consultationRepository).save(any(Consultation.class));
    }

    @Test
    void shouldThrowNotFoundWhenAppointmentDoesNotExist() {
        when(appointmentRepository.findById(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.empty());

        CreateConsultationUseCase.Command command = new CreateConsultationUseCase.Command(
                appointmentId, petId, clientId, vetId,
                null, null, null, null, null, null);

        assertThrows(NotFoundException.class, () ->
                createConsultationUseCase.execute(command));
        verify(consultationRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenAppointmentNotCompleted() {
        Appointment appointment = Appointment.withId(
                AppointmentId.from(appointmentId),
                LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Checkup",
                AppointmentStatus.PENDIENTE,
                null,
                PetId.from(petId),
                ClientId.from(clientId),
                UserId.from(vetId),
                true);
        when(appointmentRepository.findById(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.of(appointment));

        CreateConsultationUseCase.Command command = new CreateConsultationUseCase.Command(
                appointmentId, petId, clientId, vetId,
                null, null, null, null, null, null);

        assertThrows(BadRequestException.class, () ->
                createConsultationUseCase.execute(command));
        verify(consultationRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenConsultationAlreadyExists() {
        Appointment appointment = Appointment.withId(
                AppointmentId.from(appointmentId),
                LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0),
                LocalTime.of(10, 0),
                "Checkup",
                AppointmentStatus.COMPLETADA,
                null,
                PetId.from(petId),
                ClientId.from(clientId),
                UserId.from(vetId),
                true);
        when(appointmentRepository.findById(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.of(appointment));
        when(consultationRepository.findByAppointmentId(AppointmentId.from(appointmentId)))
                .thenReturn(Optional.of(mock(Consultation.class)));

        CreateConsultationUseCase.Command command = new CreateConsultationUseCase.Command(
                appointmentId, petId, clientId, vetId,
                null, null, null, null, null, null);

        assertThrows(BadRequestException.class, () ->
                createConsultationUseCase.execute(command));
        verify(consultationRepository, never()).save(any());
    }
}
