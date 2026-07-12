package com.medipet.medipet.dashboard.application;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.port.outbound.ClientNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.PetNameResolverPort;
import com.medipet.medipet.appointment.domain.port.outbound.VeterinarianNameResolverPort;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardQueryServiceImplTest {

    @Mock
    private ClientRepository clientRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PetNameResolverPort petNameResolver;
    @Mock
    private ClientNameResolverPort clientNameResolver;
    @Mock
    private VeterinarianNameResolverPort vetNameResolver;

    private DashboardQueryServiceImpl dashboardQueryService;

    @BeforeEach
    void setUp() {
        dashboardQueryService = new DashboardQueryServiceImpl(
                clientRepository, petRepository, appointmentRepository,
                petNameResolver, clientNameResolver, vetNameResolver);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private <T> Page<T> emptyPage(long totalElements) {
        return new PageImpl(Collections.emptyList(), PageRequest.of(0, 1), totalElements);
    }

    @Test
    void shouldReturnStatsWithData() {
        LocalDate today = LocalDate.now();

        when(clientRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(10));
        when(petRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(25));
        when(appointmentRepository.findByDateRange(eq(today), eq(today), eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(5));
        when(appointmentRepository.findByDateRange(eq(today.withDayOfMonth(1)),
                eq(today.withDayOfMonth(1).plusMonths(1).minusDays(1)),
                eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(30));
        when(appointmentRepository.findByDateRange(eq(today), eq(today.plusDays(7)),
                eq(PageRequest.of(0, 10))))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

        DashboardStats stats = dashboardQueryService.getStats();

        assertEquals(10, stats.totalClients());
        assertEquals(25, stats.totalPets());
        assertEquals(5, stats.appointmentsToday());
        assertEquals(30, stats.appointmentsThisMonth());
        assertTrue(stats.upcomingAppointments().isEmpty());
    }

    @Test
    void shouldReturnZeroCountsWhenNoData() {
        LocalDate today = LocalDate.now();

        when(clientRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(0));
        when(petRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(0));
        when(appointmentRepository.findByDateRange(eq(today), eq(today), eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(0));
        when(appointmentRepository.findByDateRange(eq(today.withDayOfMonth(1)),
                eq(today.withDayOfMonth(1).plusMonths(1).minusDays(1)),
                eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(0));
        when(appointmentRepository.findByDateRange(eq(today), eq(today.plusDays(7)),
                eq(PageRequest.of(0, 10))))
                .thenReturn(new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0));

        DashboardStats stats = dashboardQueryService.getStats();

        assertEquals(0, stats.totalClients());
        assertEquals(0, stats.totalPets());
        assertEquals(0, stats.appointmentsToday());
        assertEquals(0, stats.appointmentsThisMonth());
        assertTrue(stats.upcomingAppointments().isEmpty());
    }

    @Test
    void shouldIncludeUpcomingAppointments() {
        LocalDate today = LocalDate.now();
        PetId petId = PetId.generate();
        ClientId clientId = ClientId.generate();
        UserId vetId = UserId.generate();

        Appointment upcoming = Appointment.withId(
                AppointmentId.generate(), today.plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Checkup", AppointmentStatus.PENDIENTE, null,
                petId, clientId, vetId, true);

        when(clientRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(5));
        when(petRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(10));
        when(appointmentRepository.findByDateRange(eq(today), eq(today), eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(1));
        when(appointmentRepository.findByDateRange(eq(today.withDayOfMonth(1)),
                eq(today.withDayOfMonth(1).plusMonths(1).minusDays(1)),
                eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(3));
        when(appointmentRepository.findByDateRange(eq(today), eq(today.plusDays(7)),
                eq(PageRequest.of(0, 10))))
                .thenReturn(new PageImpl<>(List.of(upcoming), PageRequest.of(0, 10), 1));

        when(petNameResolver.resolveNames(any())).thenReturn(Map.of(petId, "Luna"));
        when(clientNameResolver.resolveNames(any())).thenReturn(Map.of(clientId, "Maria Garcia"));
        when(vetNameResolver.resolveNames(any())).thenReturn(Map.of(vetId, "Dr. Juan Lopez"));

        DashboardStats stats = dashboardQueryService.getStats();

        assertEquals(1, stats.upcomingAppointments().size());
        assertEquals("Luna", stats.upcomingAppointments().get(0).getPetName());
        assertEquals("Maria Garcia", stats.upcomingAppointments().get(0).getClientName());
        assertEquals("Dr. Juan Lopez", stats.upcomingAppointments().get(0).getVeterinarianName());
        assertEquals("Checkup", stats.upcomingAppointments().get(0).getReason());
    }

    @Test
    void shouldExcludeCompletedAndCancelledFromUpcoming() {
        LocalDate today = LocalDate.now();
        PetId petId = PetId.generate();
        ClientId clientId = ClientId.generate();
        UserId vetId = UserId.generate();

        Appointment completed = Appointment.withId(
                AppointmentId.generate(), today.plusDays(1),
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Done", AppointmentStatus.COMPLETADA, null,
                petId, clientId, vetId, true);
        Appointment cancelled = Appointment.withId(
                AppointmentId.generate(), today.plusDays(2),
                LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Cancelled", AppointmentStatus.CANCELADA, "No show",
                petId, clientId, vetId, true);

        when(clientRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(1));
        when(petRepository.findAll(PageRequest.of(0, 1))).thenReturn(emptyPage(1));
        when(appointmentRepository.findByDateRange(eq(today), eq(today), eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(0));
        when(appointmentRepository.findByDateRange(eq(today.withDayOfMonth(1)),
                eq(today.withDayOfMonth(1).plusMonths(1).minusDays(1)),
                eq(PageRequest.of(0, 1))))
                .thenReturn(emptyPage(0));
        when(appointmentRepository.findByDateRange(eq(today), eq(today.plusDays(7)),
                eq(PageRequest.of(0, 10))))
                .thenReturn(new PageImpl<>(List.of(completed, cancelled),
                        PageRequest.of(0, 10), 2));

        DashboardStats stats = dashboardQueryService.getStats();

        assertTrue(stats.upcomingAppointments().isEmpty());
    }
}
