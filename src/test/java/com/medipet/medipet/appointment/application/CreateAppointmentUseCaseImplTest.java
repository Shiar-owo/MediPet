package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.BadRequestException;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
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
class CreateAppointmentUseCaseImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserRepository userRepository;

    private CreateAppointmentUseCaseImpl createAppointmentUseCase;

    private PetId petId;
    private ClientId clientId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        createAppointmentUseCase = new CreateAppointmentUseCaseImpl(
                appointmentRepository, petRepository, clientRepository, userRepository);
        petId = PetId.generate();
        clientId = ClientId.generate();
        vetId = UserId.generate();
    }

    @Test
    void shouldCreateAppointmentSuccessfully() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(
                Pet.withId(petId, "Luna", "Dog", "Labrador", null, "Female", "Golden",
                        null, null, null, null, true, clientId)));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(
                Client.withId(clientId, "Maria Garcia", "12345678", "+549115551234",
                        "maria@example.com", "Av. Corrientes 1234", true)));
        when(userRepository.findById(vetId)).thenReturn(Optional.of(
                User.withId(vetId, "vet@medipet.com", "pass", "Dr. Juan", "Lopez",
                        com.medipet.medipet.user.domain.model.Role.VETERINARIO, true,
                        null, null, null, null, null)));
        when(appointmentRepository.findOverlapping(vetId, LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0))).thenReturn(Collections.emptyList());
        when(appointmentRepository.save(any(Appointment.class))).thenAnswer(inv -> inv.getArgument(0));

        Appointment result = createAppointmentUseCase.execute(
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", petId, clientId, vetId);

        assertNotNull(result);
        assertEquals(LocalDate.of(2026, 7, 10), result.getDate());
        assertEquals(LocalTime.of(9, 0), result.getStartTime());
        assertEquals(LocalTime.of(10, 0), result.getEndTime());
        assertEquals("Annual checkup", result.getReason());
        assertEquals(petId, result.getPetId());
        assertEquals(clientId, result.getClientId());
        assertEquals(vetId, result.getVeterinarianId());
        verify(appointmentRepository).save(any(Appointment.class));
    }

    @Test
    void shouldThrowNotFoundWhenPetDoesNotExist() {
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                createAppointmentUseCase.execute(
                        LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Annual checkup", petId, clientId, vetId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowNotFoundWhenVeterinarianDoesNotExist() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(
                Pet.withId(petId, "Luna", "Dog", "Labrador", null, "Female", "Golden",
                        null, null, null, null, true, clientId)));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(
                Client.withId(clientId, "Maria Garcia", "12345678", "+549115551234",
                        "maria@example.com", "Av. Corrientes 1234", true)));
        when(userRepository.findById(vetId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                createAppointmentUseCase.execute(
                        LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Annual checkup", petId, clientId, vetId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictWhenOverlapExists() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(
                Pet.withId(petId, "Luna", "Dog", "Labrador", null, "Female", "Golden",
                        null, null, null, null, true, clientId)));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(
                Client.withId(clientId, "Maria Garcia", "12345678", "+549115551234",
                        "maria@example.com", "Av. Corrientes 1234", true)));
        when(userRepository.findById(vetId)).thenReturn(Optional.of(
                User.withId(vetId, "vet@medipet.com", "pass", "Dr. Juan", "Lopez",
                        com.medipet.medipet.user.domain.model.Role.VETERINARIO, true,
                        null, null, null, null, null)));
        Appointment overlapping = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 30), LocalTime.of(10, 30), "Other", petId, clientId, vetId);
        when(appointmentRepository.findOverlapping(vetId, LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0))).thenReturn(List.of(overlapping));

        assertThrows(ConflictException.class, () ->
                createAppointmentUseCase.execute(
                        LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                        "Annual checkup", petId, clientId, vetId));

        verify(appointmentRepository, never()).save(any());
    }

    @Test
    void shouldThrowBadRequestWhenEndTimeBeforeStartTime() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(
                Pet.withId(petId, "Luna", "Dog", "Labrador", null, "Female", "Golden",
                        null, null, null, null, true, clientId)));
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(
                Client.withId(clientId, "Maria Garcia", "12345678", "+549115551234",
                        "maria@example.com", "Av. Corrientes 1234", true)));
        when(userRepository.findById(vetId)).thenReturn(Optional.of(
                User.withId(vetId, "vet@medipet.com", "pass", "Dr. Juan", "Lopez",
                        com.medipet.medipet.user.domain.model.Role.VETERINARIO, true,
                        null, null, null, null, null)));

        assertThrows(BadRequestException.class, () ->
                createAppointmentUseCase.execute(
                        LocalDate.of(2026, 7, 10), LocalTime.of(10, 0), LocalTime.of(9, 0),
                        "Annual checkup", petId, clientId, vetId));

        verify(appointmentRepository, never()).save(any());
    }
}
