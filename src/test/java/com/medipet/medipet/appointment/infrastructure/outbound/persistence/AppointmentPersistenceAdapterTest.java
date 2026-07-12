package com.medipet.medipet.appointment.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.appointment.domain.model.Appointment;
import com.medipet.medipet.appointment.domain.model.AppointmentStatus;
import com.medipet.medipet.appointment.domain.port.outbound.AppointmentRepository;
import com.medipet.medipet.appointment.domain.valueobject.AppointmentId;
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

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class AppointmentPersistenceAdapterTest {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private UserRepository userRepository;

    private ClientId clientId;
    private PetId petId;
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
                Role.VETERINARIO, null, null, null, null);
        User savedVet = userRepository.save(vet);
        vetId = savedVet.getId();
    }

    @Test
    void shouldSaveAndFindById() {
        Appointment appointment = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Annual checkup",
                petId, clientId, vetId);
        Appointment saved = appointmentRepository.save(appointment);

        Optional<Appointment> found = appointmentRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals(LocalDate.of(2026, 7, 10), found.get().getDate());
        assertEquals(LocalTime.of(9, 0), found.get().getStartTime());
        assertEquals(LocalTime.of(10, 0), found.get().getEndTime());
        assertEquals("Annual checkup", found.get().getReason());
        assertEquals(AppointmentStatus.PENDIENTE, found.get().getStatus());
        assertEquals(petId, found.get().getPetId());
        assertEquals(clientId, found.get().getClientId());
        assertEquals(vetId, found.get().getVeterinarianId());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldFindOverlapping() {
        Appointment appointment1 = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup",
                petId, clientId, vetId);
        appointmentRepository.save(appointment1);

        List<Appointment> overlapping = appointmentRepository.findOverlapping(
                vetId, LocalDate.of(2026, 7, 10), LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertEquals(1, overlapping.size());
        assertEquals(appointment1.getId(), overlapping.get(0).getId());
    }

    @Test
    void shouldNotFindOverlappingWhenCancelled() {
        Appointment appointment = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup",
                petId, clientId, vetId);
        Appointment saved = appointmentRepository.save(appointment);

        saved.setStatus(AppointmentStatus.CANCELADA);
        appointmentRepository.save(saved);

        List<Appointment> overlapping = appointmentRepository.findOverlapping(
                vetId, LocalDate.of(2026, 7, 10), LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertTrue(overlapping.isEmpty());
    }

    @Test
    void shouldNotFindOverlappingWhenDifferentVet() {
        User otherVet = User.create("other@medipet.com", "password123", "Dr. Ana", "Martinez",
                Role.VETERINARIO, null, null, null, null);
        User savedOtherVet = userRepository.save(otherVet);

        Appointment appointment = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup",
                petId, clientId, vetId);
        appointmentRepository.save(appointment);

        List<Appointment> overlapping = appointmentRepository.findOverlapping(
                savedOtherVet.getId(), LocalDate.of(2026, 7, 10), LocalTime.of(9, 30), LocalTime.of(10, 30));

        assertTrue(overlapping.isEmpty());
    }

    @Test
    void shouldFindByClientId() {
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup 1", petId, clientId, vetId));
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 11),
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Checkup 2", petId, clientId, vetId));

        Page<Appointment> page = appointmentRepository.findByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(2, page.getContent().size());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void shouldFindByPetId() {
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup", petId, clientId, vetId));

        Page<Appointment> page = appointmentRepository.findByPetId(petId, PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertEquals(petId, page.getContent().get(0).getPetId());
    }

    @Test
    void shouldFindByVeterinarianId() {
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup", petId, clientId, vetId));

        Page<Appointment> page = appointmentRepository.findByVeterinarianId(vetId, PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertEquals(vetId, page.getContent().get(0).getVeterinarianId());
    }

    @Test
    void shouldFindByDateRange() {
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Morning", petId, clientId, vetId));
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 15),
                LocalTime.of(14, 0), LocalTime.of(15, 0), "Afternoon", petId, clientId, vetId));
        appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 20),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Later", petId, clientId, vetId));

        Page<Appointment> page = appointmentRepository.findByDateRange(
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 14), PageRequest.of(0, 10));

        assertEquals(1, page.getContent().size());
        assertEquals(LocalDate.of(2026, 7, 10), page.getContent().get(0).getDate());
    }

    @Test
    void shouldFindAllPaginated() {
        for (int i = 0; i < 5; i++) {
            appointmentRepository.save(Appointment.create(LocalDate.of(2026, 7, 10 + i),
                    LocalTime.of(9, 0), LocalTime.of(10, 0), "Checkup " + i,
                    petId, clientId, vetId));
        }

        Page<Appointment> page = appointmentRepository.findAll(PageRequest.of(0, 3));

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void shouldUpdateExistingAppointment() {
        Appointment appointment = Appointment.create(LocalDate.of(2026, 7, 10),
                LocalTime.of(9, 0), LocalTime.of(10, 0), "Original reason",
                petId, clientId, vetId);
        Appointment saved = appointmentRepository.save(appointment);

        saved.setReason("Updated reason");
        saved.setStatus(AppointmentStatus.CONFIRMADA);
        appointmentRepository.save(saved);

        Optional<Appointment> found = appointmentRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("Updated reason", found.get().getReason());
        assertEquals(AppointmentStatus.CONFIRMADA, found.get().getStatus());
    }
}
