package com.medipet.medipet.clinical.infrastructure.inbound.rest;

import com.medipet.medipet.clinical.application.ConsultationSummary;
import com.medipet.medipet.clinical.application.port.inbound.ConsultationQueryService;
import com.medipet.medipet.clinical.application.port.inbound.CreateConsultationUseCase;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.port.outbound.TokenProvider;
import com.medipet.medipet.user.domain.valueobject.UserId;
import com.medipet.medipet.user.infrastructure.inbound.security.JwtAuthenticationFilter;
import com.medipet.medipet.user.infrastructure.outbound.security.SecurityUserDetailsService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ConsultationController.class)
@AutoConfigureMockMvc(addFilters = false)
class ConsultationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CreateConsultationUseCase createConsultationUseCase;

    @MockitoBean
    private ConsultationQueryService consultationQueryService;

    @MockitoBean
    private TokenProvider tokenProvider;

    @MockitoBean
    private SecurityUserDetailsService securityUserDetailsService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    private static final String PET_ID = "b2c3d4e5-f6a7-8901-bcde-f12345678901";
    private static final String CLIENT_ID = "c3d4e5f6-a7b8-9012-cdef-123456789012";
    private static final String VET_ID = "d4e5f6a7-b8c9-0123-defa-234567890123";
    private static final String APPOINTMENT_ID = "e5f6a7b8-c9d0-1234-efab-345678901234";

    private ConsultationSummary createTestSummary(String id) {
        return new ConsultationSummary(
                UUID.fromString(id),
                UUID.fromString(APPOINTMENT_ID),
                UUID.fromString(PET_ID),
                "Luna",
                UUID.fromString(CLIENT_ID),
                "Maria Garcia",
                UUID.fromString(VET_ID),
                "Dr. Juan Lopez",
                new BigDecimal("5.5"),
                new BigDecimal("38.5"),
                "Vomiting",
                "Gastroenteritis",
                "Antibiotics",
                "Follow up",
                true,
                null,
                null);
    }

    @Test
    void shouldCreateConsultation() throws Exception {
        String id = UUID.randomUUID().toString();
        Consultation consultation = Consultation.create(
                AppointmentId.from(APPOINTMENT_ID),
                PetId.from(PET_ID),
                ClientId.from(CLIENT_ID),
                UserId.from(VET_ID),
                new BigDecimal("5.5"),
                new BigDecimal("38.5"),
                "Vomiting",
                "Gastroenteritis",
                "Antibiotics",
                "Follow up");
        ConsultationSummary summary = createTestSummary(id);

        when(createConsultationUseCase.execute(any(CreateConsultationUseCase.Command.class)))
                .thenReturn(consultation);
        when(consultationQueryService.getById(consultation.getId())).thenReturn(summary);

        mockMvc.perform(post("/api/consultations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"appointmentId":"%s","petId":"%s","clientId":"%s","veterinarianId":"%s","weight":5.5,"temperature":38.5,"symptoms":"Vomiting","diagnosis":"Gastroenteritis","treatment":"Antibiotics","notes":"Follow up"}
                                """.formatted(APPOINTMENT_ID, PET_ID, CLIENT_ID, VET_ID)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.symptoms").value("Vomiting"))
                .andExpect(jsonPath("$.data.diagnosis").value("Gastroenteritis"))
                .andExpect(jsonPath("$.data.petName").value("Luna"));
    }

    @Test
    void shouldGetConsultationById() throws Exception {
        String id = UUID.randomUUID().toString();
        ConsultationSummary summary = createTestSummary(id);

        when(consultationQueryService.getById(ConsultationId.from(id))).thenReturn(summary);

        mockMvc.perform(get("/api/consultations/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.diagnosis").value("Gastroenteritis"))
                .andExpect(jsonPath("$.data.symptoms").value("Vomiting"));
    }

    @Test
    void shouldGetConsultationByAppointmentId() throws Exception {
        ConsultationSummary summary = createTestSummary(UUID.randomUUID().toString());

        when(consultationQueryService.getByAppointmentId(AppointmentId.from(APPOINTMENT_ID)))
                .thenReturn(summary);

        mockMvc.perform(get("/api/consultations/appointment/{appointmentId}", APPOINTMENT_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.appointmentId").value(APPOINTMENT_ID));
    }

    @Test
    void shouldGetConsultationsByPetId() throws Exception {
        String id = UUID.randomUUID().toString();
        ConsultationSummary summary = createTestSummary(id);
        Page<ConsultationSummary> page = new PageImpl<>(
                List.of(summary), PageRequest.of(0, 20), 1);

        when(consultationQueryService.getByPetId(eq(PetId.from(PET_ID)), any(PageRequest.class)))
                .thenReturn(page);

        mockMvc.perform(get("/api/consultations/pet/{petId}", PET_ID)
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content[0].diagnosis").value("Gastroenteritis"))
                .andExpect(jsonPath("$.data.content[0].petName").value("Luna"));
    }
}
