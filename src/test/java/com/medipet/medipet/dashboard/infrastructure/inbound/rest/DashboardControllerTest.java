package com.medipet.medipet.dashboard.infrastructure.inbound.rest;

import com.medipet.medipet.appointment.application.AppointmentSummary;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.dashboard.application.DashboardStats;
import com.medipet.medipet.dashboard.application.port.inbound.DashboardQueryService;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DashboardController.class)
@AutoConfigureMockMvc(addFilters = false)
class DashboardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DashboardQueryService dashboardQueryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void shouldReturnStats() throws Exception {
        DashboardStats stats = new DashboardStats(10, 25, 5, 30, List.of());
        when(dashboardQueryService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalClients").value(10))
                .andExpect(jsonPath("$.data.totalPets").value(25))
                .andExpect(jsonPath("$.data.appointmentsToday").value(5))
                .andExpect(jsonPath("$.data.appointmentsThisMonth").value(30))
                .andExpect(jsonPath("$.data.upcomingAppointments").isEmpty());
    }

    @Test
    void shouldReturnStatsWithUpcomingAppointments() throws Exception {
        UUID id = UUID.randomUUID();
        AppointmentSummary summary = mock(AppointmentSummary.class);
        when(summary.getId()).thenReturn(AppointmentId.from(id));
        when(summary.getDate()).thenReturn(LocalDate.now().plusDays(1));
        when(summary.getStartTime()).thenReturn(LocalTime.of(9, 0));
        when(summary.getEndTime()).thenReturn(LocalTime.of(10, 0));
        when(summary.getReason()).thenReturn("Checkup");
        when(summary.getStatus()).thenReturn(AppointmentStatus.PENDIENTE);
        when(summary.getPetId()).thenReturn(PetId.generate());
        when(summary.getPetName()).thenReturn("Luna");
        when(summary.getClientId()).thenReturn(ClientId.generate());
        when(summary.getClientName()).thenReturn("Maria Garcia");
        when(summary.getVeterinarianId()).thenReturn(UserId.generate());
        when(summary.getVeterinarianName()).thenReturn("Dr. Juan Lopez");
        when(summary.isActive()).thenReturn(true);

        DashboardStats stats = new DashboardStats(10, 25, 5, 30, List.of(summary));
        when(dashboardQueryService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.upcomingAppointments[0].petName").value("Luna"))
                .andExpect(jsonPath("$.data.upcomingAppointments[0].clientName").value("Maria Garcia"))
                .andExpect(jsonPath("$.data.upcomingAppointments[0].veterinarianName").value("Dr. Juan Lopez"))
                .andExpect(jsonPath("$.data.upcomingAppointments[0].reason").value("Checkup"));
    }

    @Test
    void shouldReturnZeroCountsWhenNoData() throws Exception {
        DashboardStats stats = new DashboardStats(0, 0, 0, 0, List.of());
        when(dashboardQueryService.getStats()).thenReturn(stats);

        mockMvc.perform(get("/api/dashboard/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.totalClients").value(0))
                .andExpect(jsonPath("$.data.totalPets").value(0))
                .andExpect(jsonPath("$.data.appointmentsToday").value(0))
                .andExpect(jsonPath("$.data.appointmentsThisMonth").value(0))
                .andExpect(jsonPath("$.data.upcomingAppointments").isEmpty());
    }
}
