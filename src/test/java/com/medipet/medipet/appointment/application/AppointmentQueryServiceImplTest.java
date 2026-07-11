package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.inbound.AppointmentAuthorizationPort;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentQueryServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    @Mock
    private AppointmentAuthorizationPort authorizationPort;

    private AppointmentQueryServiceImpl appointmentQueryService;

    private AppointmentId appointmentId;
    private Appointment testAppointment;
    private ClientId clientId;
    private PetId petId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        appointmentQueryService = new AppointmentQueryServiceImpl(appointmentRepository, authorizationPort);
        appointmentId = AppointmentId.generate();
        clientId = ClientId.generate();
        petId = PetId.generate();
        vetId = UserId.generate();
        testAppointment = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.PENDIENTE, null,
                petId, clientId, vetId, true);
    }

    @Test
    void shouldFindById() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));

        Appointment result = appointmentQueryService.findById(appointmentId);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
        assertEquals("Annual checkup", result.getReason());
    }

    @Test
    void shouldThrowWhenNotFoundById() {
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                appointmentQueryService.findById(appointmentId));
    }

    @Test
    void shouldThrowForbiddenWhenVetViewsOtherVetAppointment() {
        UserId otherVetId = UserId.generate();
        Appointment otherVetAppointment = Appointment.withId(appointmentId,
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.PENDIENTE, null,
                petId, clientId, otherVetId, true);

        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(otherVetAppointment));

        assertThrows(ForbiddenException.class, () ->
                appointmentQueryService.findById(appointmentId));
    }

    @Test
    void shouldAllowVetToViewOwnAppointment() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(testAppointment));

        Appointment result = appointmentQueryService.findById(appointmentId);

        assertNotNull(result);
        assertEquals(appointmentId, result.getId());
    }

    @Test
    void shouldFindByClientId() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByClientId(clientId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals("Annual checkup", result.getContent().get(0).getReason());
    }

    @Test
    void shouldFindByPetId() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByPetId(petId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findByPetId(petId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindByVeterinarianId() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByVeterinarianId(vetId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findByVeterinarianId(vetId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldFindByDateRange() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByDateRange(LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 7, 31), PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findByDateRange(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 31), PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void shouldReturnAllWhenQueryIsNull() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findAll(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findAll(PageRequest.of(0, 20));
    }

    // ========== Role-aware method tests ==========

    @Test
    void shouldFindForCurrentUserByClientIdAsAdmin() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByClientId(clientId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByClientId(clientId, PageRequest.of(0, 10));
        verify(appointmentRepository, never()).findByVeterinarianIdAndClientId(any(), any(), any());
    }

    @Test
    void shouldFindForCurrentUserByClientIdAsVet() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByVeterinarianIdAndClientId(vetId, clientId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByVeterinarianIdAndClientId(vetId, clientId, PageRequest.of(0, 10));
        verify(appointmentRepository, never()).findByClientId(any(), any());
    }

    @Test
    void shouldFindForCurrentUserByPetIdAsAdmin() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByPetId(petId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByPetId(petId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByPetId(petId, PageRequest.of(0, 10));
        verify(appointmentRepository, never()).findByVeterinarianIdAndPetId(any(), any(), any());
    }

    @Test
    void shouldFindForCurrentUserByPetIdAsVet() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByVeterinarianIdAndPetId(vetId, petId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByPetId(petId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByVeterinarianIdAndPetId(vetId, petId, PageRequest.of(0, 10));
        verify(appointmentRepository, never()).findByPetId(any(), any());
    }

    @Test
    void shouldFindForCurrentUserAsVet() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.VETERINARIO);
        when(authorizationPort.getCurrentUserId()).thenReturn(vetId);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findByVeterinarianId(vetId, PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUser(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByVeterinarianId(vetId, PageRequest.of(0, 20));
    }

    @Test
    void shouldFindForCurrentUserAsAdminWithDate() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        LocalDate date = LocalDate.of(2026, 7, 10);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findByDateRange(date, date, PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUser(date, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByDateRange(date, date, PageRequest.of(0, 20));
    }

    @Test
    void shouldFindForCurrentUserAsAdminWithoutDate() {
        when(authorizationPort.getCurrentUserRole()).thenReturn(Role.ADMIN);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUser(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findAll(PageRequest.of(0, 20));
    }
}
