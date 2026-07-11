package com.medipet.medipet.appointment.infrastructure.inbound.rest;

import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.inbound.AppointmentQueryService;
import com.medipet.medipet.appointment.domain.port.inbound.CancelAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.inbound.CreateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.port.inbound.UpdateAppointmentUseCase;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.ForbiddenException;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.startsWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AppointmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class AppointmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateAppointmentUseCase createAppointmentUseCase;

    @MockitoBean
    private UpdateAppointmentUseCase updateAppointmentUseCase;

    @MockitoBean
    private CancelAppointmentUseCase cancelAppointmentUseCase;

    @MockitoBean
    private AppointmentQueryService appointmentQueryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String VET_ID = "d4e5f6a7-b8c9-0123-defa-234567890123";
    private static final String OTHER_VET_ID = "e5f6a7b8-c9d0-1234-efab-345678901234";

    private Appointment createTestAppointment(String id) {
        return Appointment.withId(AppointmentId.from(UUID.fromString(id)),
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.PENDIENTE, null,
                PetId.from(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901")),
                ClientId.from(UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012")),
                UserId.from(UUID.fromString(VET_ID)),
                true);
    }

    // ========== CRUD tests ==========

    @Test
    void shouldCreateAppointment() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment appointment = createTestAppointment(id);

        when(createAppointmentUseCase.execute(
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup",
                PetId.from("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
                ClientId.from("c3d4e5f6-a7b8-9012-cdef-123456789012"),
                UserId.from(VET_ID)))
                .thenReturn(appointment);

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-10","startTime":"09:00","endTime":"10:00","reason":"Annual checkup","petId":"b2c3d4e5-f6a7-8901-bcde-f12345678901","clientId":"c3d4e5f6-a7b8-9012-cdef-123456789012","veterinarianId":"%s"}
                                """.formatted(VET_ID)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", startsWith("/api/appointments/")))
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.date").value("2026-07-10"))
                .andExpect(jsonPath("$.data.startTime").value("09:00"))
                .andExpect(jsonPath("$.data.status").value("PENDIENTE"));
    }

    @Test
    void shouldReturn409WhenOverlapExists() throws Exception {
        when(createAppointmentUseCase.execute(
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup",
                PetId.from("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
                ClientId.from("c3d4e5f6-a7b8-9012-cdef-123456789012"),
                UserId.from(VET_ID)))
                .thenThrow(new ConflictException("Appointment overlaps with an existing appointment"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-10","startTime":"09:00","endTime":"10:00","reason":"Annual checkup","petId":"b2c3d4e5-f6a7-8901-bcde-f12345678901","clientId":"c3d4e5f6-a7b8-9012-cdef-123456789012","veterinarianId":"%s"}
                                """.formatted(VET_ID)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldUpdateAppointment() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment updated = Appointment.withId(AppointmentId.from(UUID.fromString(id)),
                LocalDate.of(2026, 7, 11), LocalTime.of(14, 0), LocalTime.of(15, 0),
                "Vaccination", AppointmentStatus.PENDIENTE, null,
                PetId.from(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901")),
                ClientId.from(UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012")),
                UserId.from(UUID.fromString(VET_ID)),
                true);

        when(updateAppointmentUseCase.execute(AppointmentId.from(id),
                LocalDate.of(2026, 7, 11), LocalTime.of(14, 0), LocalTime.of(15, 0),
                "Vaccination"))
                .thenReturn(updated);

        mockMvc.perform(put("/api/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-11","startTime":"14:00","endTime":"15:00","reason":"Vaccination"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.date").value("2026-07-11"))
                .andExpect(jsonPath("$.data.startTime").value("14:00"))
                .andExpect(jsonPath("$.data.reason").value("Vaccination"));
    }

    @Test
    void shouldReturn404WhenAppointmentNotFoundForUpdate() throws Exception {
        String id = UUID.randomUUID().toString();
        when(updateAppointmentUseCase.execute(
                eq(AppointmentId.from(id)),
                eq(LocalDate.of(2026, 7, 11)), isNull(), isNull(),
                eq("Vaccination")))
                .thenThrow(new NotFoundException("Appointment", id));

        mockMvc.perform(put("/api/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-11","reason":"Vaccination"}
                                """))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldCancelAppointment() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment cancelled = Appointment.withId(AppointmentId.from(UUID.fromString(id)),
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup", AppointmentStatus.CANCELADA, "Client requested",
                PetId.from(UUID.fromString("b2c3d4e5-f6a7-8901-bcde-f12345678901")),
                ClientId.from(UUID.fromString("c3d4e5f6-a7b8-9012-cdef-123456789012")),
                UserId.from(UUID.fromString(VET_ID)),
                true);

        when(cancelAppointmentUseCase.execute(AppointmentId.from(id), "Client requested"))
                .thenReturn(cancelled);

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cancelReason":"Client requested"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.status").value("CANCELADA"))
                .andExpect(jsonPath("$.data.cancelReason").value("Client requested"));
    }

    @Test
    void shouldGetAppointmentById() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment appointment = createTestAppointment(id);

        when(appointmentQueryService.findById(AppointmentId.from(id))).thenReturn(appointment);

        mockMvc.perform(get("/api/appointments/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.date").value("2026-07-10"))
                .andExpect(jsonPath("$.data.reason").value("Annual checkup"));
    }

    @Test
    void shouldReturn404WhenAppointmentNotFoundById() throws Exception {
        String id = UUID.randomUUID().toString();
        when(appointmentQueryService.findById(AppointmentId.from(id)))
                .thenThrow(new NotFoundException("Appointment", id));

        mockMvc.perform(get("/api/appointments/{id}", id))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldListAppointments() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment appointment = createTestAppointment(id);
        PageResponse<Appointment> page = new PageResponse<>(List.of(appointment), 0, 20, 1, 1);

        when(appointmentQueryService.findAll(any(PageRequest.class))).thenReturn(page);

        mockMvc.perform(get("/api/appointments").param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-07-10"));
    }

    @Test
    void shouldRejectCreateWithBlankDate() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"","startTime":"09:00","endTime":"10:00","petId":"b2c3d4e5-f6a7-8901-bcde-f12345678901","clientId":"c3d4e5f6-a7b8-9012-cdef-123456789012","veterinarianId":"%s"}
                                """.formatted(VET_ID)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectCreateWithBlankPetId() throws Exception {
        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-10","startTime":"09:00","endTime":"10:00","petId":"","clientId":"c3d4e5f6-a7b8-9012-cdef-123456789012","veterinarianId":"%s"}
                                """.formatted(VET_ID)))
                .andExpect(status().isBadRequest());
    }

    // ========== View by veterinarian ==========

    @Test
    void shouldListAppointmentsByVeterinarian() throws Exception {
        String id = UUID.randomUUID().toString();
        Appointment appointment = createTestAppointment(id);
        PageResponse<Appointment> page = new PageResponse<>(List.of(appointment), 0, 20, 1, 1);

        when(appointmentQueryService.findByVeterinarianId(
                eq(UserId.from(VET_ID)), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/appointments/veterinarian/{vetId}", VET_ID)
                        .param("page", "0").param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].date").value("2026-07-10"));
    }

    // ========== Use case exception tests (auth enforced in use cases) ==========

    @Test
    void shouldReturn403WhenVetCreatesForOtherVet() throws Exception {
        when(createAppointmentUseCase.execute(
                LocalDate.of(2026, 7, 10), LocalTime.of(9, 0), LocalTime.of(10, 0),
                "Annual checkup",
                PetId.from("b2c3d4e5-f6a7-8901-bcde-f12345678901"),
                ClientId.from("c3d4e5f6-a7b8-9012-cdef-123456789012"),
                UserId.from(OTHER_VET_ID)))
                .thenThrow(new ForbiddenException("Veterinarians can only create appointments for themselves"));

        mockMvc.perform(post("/api/appointments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-10","startTime":"09:00","endTime":"10:00","reason":"Annual checkup","petId":"b2c3d4e5-f6a7-8901-bcde-f12345678901","clientId":"c3d4e5f6-a7b8-9012-cdef-123456789012","veterinarianId":"%s"}
                                """.formatted(OTHER_VET_ID)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn403WhenVetUpdatesOtherVetAppointment() throws Exception {
        String id = UUID.randomUUID().toString();

        when(updateAppointmentUseCase.execute(AppointmentId.from(id),
                LocalDate.of(2026, 7, 11), null, null, "Vaccination"))
                .thenThrow(new ForbiddenException("Veterinarians can only access their own appointments"));

        mockMvc.perform(put("/api/appointments/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"date":"2026-07-11","reason":"Vaccination"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }

    @Test
    void shouldReturn403WhenVetCancelsOtherVetAppointment() throws Exception {
        String id = UUID.randomUUID().toString();

        when(cancelAppointmentUseCase.execute(AppointmentId.from(id), "Client requested"))
                .thenThrow(new ForbiddenException("Veterinarians can only access their own appointments"));

        mockMvc.perform(patch("/api/appointments/{id}/cancel", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"cancelReason":"Client requested"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.error.code").value("FORBIDDEN"));
    }
}
