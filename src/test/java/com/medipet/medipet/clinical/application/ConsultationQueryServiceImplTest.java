package com.medipet.medipet.clinical.application;

import com.medipet.medipet.clinical.application.port.inbound.ConsultationQueryService;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultationQueryServiceImplTest {

    @Mock
    private ConsultationRepository consultationRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private ClientRepository clientRepository;
    @Mock
    private UserRepository userRepository;

    private ConsultationQueryService consultationQueryService;

    private ConsultationId consultationId;
    private AppointmentId appointmentId;
    private PetId petId;
    private ClientId clientId;
    private UserId vetId;
    private Consultation testConsultation;

    @BeforeEach
    void setUp() {
        consultationQueryService = new ConsultationQueryServiceImpl(
                consultationRepository, petRepository, clientRepository, userRepository);
        consultationId = ConsultationId.generate();
        appointmentId = AppointmentId.generate();
        petId = PetId.generate();
        clientId = ClientId.generate();
        vetId = UserId.generate();

        testConsultation = Consultation.withId(
                consultationId, appointmentId, petId, clientId, vetId,
                new BigDecimal("5.5"), new BigDecimal("38.5"),
                "Vomiting", "Gastroenteritis", "Antibiotics", "Follow up", true);

        lenient().when(petRepository.findById(petId)).thenReturn(Optional.of(
                Pet.withId(petId, "Luna", "Dog", "Labrador", null, "Female", "Golden",
                        null, null, null, null, true, clientId)));
        lenient().when(clientRepository.findById(clientId)).thenReturn(Optional.of(
                Client.withId(clientId, "Maria Garcia", "12345678", "+549115551234",
                        "maria@example.com", "Av. Corrientes 1234", true)));
        lenient().when(userRepository.findById(vetId)).thenReturn(Optional.of(
                User.withId(vetId, "vet@medipet.com", "pass", "Dr. Juan", "Lopez",
                        com.medipet.medipet.user.domain.model.Role.VETERINARIO, true,
                        null, null, null, null, null)));
    }

    @Test
    void shouldGetById() {
        when(consultationRepository.findById(consultationId))
                .thenReturn(Optional.of(testConsultation));

        ConsultationSummary result = consultationQueryService.getById(consultationId);

        assertNotNull(result);
        assertEquals(consultationId.getValue(), result.id());
        assertEquals("Gastroenteritis", result.diagnosis());
        assertEquals("Luna", result.petName());
        assertEquals("Maria Garcia", result.clientName());
        assertEquals("Dr. Juan Lopez", result.veterinarianName());
    }

    @Test
    void shouldThrowNotFoundWhenByIdNotFound() {
        when(consultationRepository.findById(consultationId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                consultationQueryService.getById(consultationId));
    }

    @Test
    void shouldGetByAppointmentId() {
        when(consultationRepository.findByAppointmentId(appointmentId))
                .thenReturn(Optional.of(testConsultation));

        ConsultationSummary result = consultationQueryService.getByAppointmentId(appointmentId);

        assertNotNull(result);
        assertEquals(appointmentId.getValue(), result.appointmentId());
        assertEquals("Vomiting", result.symptoms());
    }

    @Test
    void shouldGetByPetId() {
        Page<Consultation> page = new PageImpl<>(
                List.of(testConsultation), PageRequest.of(0, 10), 1);
        when(consultationRepository.findByPetId(petId, PageRequest.of(0, 10)))
                .thenReturn(page);

        Page<ConsultationSummary> result = consultationQueryService.getByPetId(
                petId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals("Gastroenteritis", result.getContent().get(0).diagnosis());
    }
}
