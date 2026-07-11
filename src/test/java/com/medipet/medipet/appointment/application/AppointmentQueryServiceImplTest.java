package com.medipet.medipet.appointment.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentQueryServiceImplTest {

    @Mock
    private AppointmentRepository appointmentRepository;

    private AppointmentQueryServiceImpl appointmentQueryService;

    private AppointmentId appointmentId;
    private Appointment testAppointment;
    private ClientId clientId;
    private PetId petId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        appointmentQueryService = new AppointmentQueryServiceImpl(appointmentRepository);
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

    // ========== findForCurrentUser tests ==========

    @Test
    void shouldFindForCurrentUserByClientId() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByClientId(clientId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByClientId(clientId, PageRequest.of(0, 10));
    }

    @Test
    void shouldFindForCurrentUserByPetId() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 10), 1);
        when(appointmentRepository.findByPetId(petId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUserByPetId(petId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByPetId(petId, PageRequest.of(0, 10));
    }

    @Test
    void shouldFindForCurrentUserWithDate() {
        LocalDate date = LocalDate.of(2026, 7, 10);
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findByDateRange(date, date, PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUser(date, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findByDateRange(date, date, PageRequest.of(0, 20));
    }

    @Test
    void shouldFindForCurrentUserWithoutDate() {
        Page<Appointment> page = new PageImpl<>(List.of(testAppointment), PageRequest.of(0, 20), 1);
        when(appointmentRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Appointment> result = appointmentQueryService.findForCurrentUser(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(appointmentRepository).findAll(PageRequest.of(0, 20));
    }
}
