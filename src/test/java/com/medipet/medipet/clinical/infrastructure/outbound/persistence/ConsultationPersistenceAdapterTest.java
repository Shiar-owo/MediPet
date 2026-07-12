package com.medipet.medipet.clinical.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
import com.medipet.medipet.clinical.domain.model.Consultation;
import com.medipet.medipet.clinical.domain.port.outbound.ConsultationRepository;
import com.medipet.medipet.clinical.domain.valueobject.ConsultationId;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.user.domain.model.Role;
import com.medipet.medipet.user.domain.model.User;
import com.medipet.medipet.user.domain.port.outbound.UserRepository;
import com.medipet.medipet.user.domain.valueobject.UserId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class ConsultationPersistenceAdapterTest {

    @Autowired
    private ConsultationRepository consultationRepository;

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private AppointmentId appointmentId;
    private PetId petId;
    private ClientId clientId;
    private UserId vetId;

    @BeforeEach
    void setUp() {
        Client owner = Client.create("Maria Garcia", "12345678",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234");
        Client savedClient = clientRepository.save(owner);
        clientId = savedClient.getId();

        Pet pet = Pet.create("Luna", "Dog", "Labrador", null, "Female", "Golden",
                null, null, null, null, clientId);
        Pet savedPet = petRepository.save(pet);
        petId = savedPet.getId();

        User vet = User.create("vet@medipet.com", "password123", "Dr. Juan", "Lopez",
                Role.VETERINARIO, null, null, null);
        User savedVet = userRepository.save(vet);
        vetId = savedVet.getId();

        Appointment appointment = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup",
                petId, clientId, vetId);
        appointment.setStatus(AppointmentStatus.COMPLETADA);
        Appointment savedAppointment = appointmentRepository.save(appointment);
        appointmentId = savedAppointment.getId();
    }

    @Test
    void shouldSaveAndFindById() {
        Consultation consultation = Consultation.create(
                appointmentId, petId, clientId, vetId,
                new BigDecimal("5.5"), new BigDecimal("38.5"),
                "Vomiting", "Gastroenteritis", "Antibiotics", "Follow up");

        Consultation saved = consultationRepository.save(consultation);

        Optional<Consultation> found = consultationRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(consultation.getAppointmentId(), found.get().getAppointmentId());
        assertEquals(consultation.getPetId(), found.get().getPetId());
        assertEquals(consultation.getClientId(), found.get().getClientId());
        assertEquals(consultation.getVeterinarianId(), found.get().getVeterinarianId());
        assertEquals(new BigDecimal("5.5"), found.get().getWeight());
        assertEquals(new BigDecimal("38.5"), found.get().getTemperature());
        assertEquals("Vomiting", found.get().getSymptoms());
        assertEquals("Gastroenteritis", found.get().getDiagnosis());
        assertEquals("Antibiotics", found.get().getTreatment());
        assertEquals("Follow up", found.get().getNotes());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldFindByAppointmentId() {
        Consultation consultation = Consultation.create(
                appointmentId, petId, clientId, vetId,
                null, null, "Symptoms", "Diagnosis", null, null);
        consultationRepository.save(consultation);

        Optional<Consultation> found = consultationRepository.findByAppointmentId(appointmentId);

        assertTrue(found.isPresent());
        assertEquals("Diagnosis", found.get().getDiagnosis());
    }

    @Test
    void shouldFindByPetId() {
        Consultation consultation1 = Consultation.create(
                appointmentId, petId, clientId, vetId,
                null, null, "Symptoms 1", "Diagnosis 1", null, null);
        consultationRepository.save(consultation1);

        Appointment appointment2 = Appointment.create(LocalDate.of(2026, 7, 15),
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Follow-up",
                petId, clientId, vetId);
        appointment2.setStatus(AppointmentStatus.COMPLETADA);
        Appointment savedAppointment2 = appointmentRepository.save(appointment2);

        Consultation consultation2 = Consultation.create(
                savedAppointment2.getId(), petId, clientId, vetId,
                null, null, "Symptoms 2", "Diagnosis 2", null, null);
        consultationRepository.save(consultation2);

        Page<Consultation> page = consultationRepository.findByPetId(
                petId, PageRequest.of(0, 10));

        assertEquals(2, page.getContent().size());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void shouldUpdateExistingConsultation() {
        Consultation consultation = Consultation.create(
                appointmentId, petId, clientId, vetId,
                new BigDecimal("5.5"), null, "Symptoms", "Diagnosis", null, null);
        Consultation saved = consultationRepository.save(consultation);

        saved.setDiagnosis("Updated diagnosis");
        saved.setTreatment("New treatment");
        saved.setWeight(new BigDecimal("6.0"));
        consultationRepository.save(saved);

        Optional<Consultation> found = consultationRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated diagnosis", found.get().getDiagnosis());
        assertEquals("New treatment", found.get().getTreatment());
        assertEquals(new BigDecimal("6.0"), found.get().getWeight());
    }
}
