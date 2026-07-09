package com.medipet.medipet.pet.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
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
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class PetPersistenceAdapterTest {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ClientRepository clientRepository;

    private ClientId clientId;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        Client owner = Client.create("Maria Garcia", "87654321",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234");
        Client saved = clientRepository.save(owner);
        clientId = saved.getId();

        testPet = Pet.create("Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, clientId);
    }

    @Test
    void shouldSaveAndFindById() {
        Pet saved = petRepository.save(testPet);

        Optional<Pet> found = petRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Max", found.get().getName());
        assertEquals("Dog", found.get().getSpecies());
        assertEquals("Golden Retriever", found.get().getBreed());
        assertEquals("MC12345", found.get().getMicrochipId());
        assertEquals("TAT001", found.get().getTattooNumber());
        assertEquals(clientId, found.get().getClientId());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldFindByMicrochipId() {
        petRepository.save(testPet);

        Optional<Pet> found = petRepository.findByMicrochipId("MC12345");

        assertTrue(found.isPresent());
        assertEquals("Max", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenMicrochipNotFound() {
        Optional<Pet> found = petRepository.findByMicrochipId("NONEXISTENT");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldFindByTattooNumber() {
        petRepository.save(testPet);

        Optional<Pet> found = petRepository.findByTattooNumber("TAT001");

        assertTrue(found.isPresent());
        assertEquals("Max", found.get().getName());
    }

    @Test
    void shouldReturnTrueWhenMicrochipExists() {
        petRepository.save(testPet);

        assertTrue(petRepository.existsByMicrochipId("MC12345"));
    }

    @Test
    void shouldReturnFalseWhenMicrochipDoesNotExist() {
        assertFalse(petRepository.existsByMicrochipId("NONEXISTENT"));
    }

    @Test
    void shouldFindByClientId() {
        petRepository.save(testPet);
        petRepository.save(Pet.create("Bella", "Cat", "Persian",
                LocalDate.of(2021, 3, 10), "FEMALE", "White", BigDecimal.valueOf(4.2),
                "MC99999", null, null, clientId));

        Page<Pet> page = petRepository.findByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(2, page.getContent().size());
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void shouldFindAllPaginated() {
        petRepository.save(testPet);
        for (int i = 0; i < 4; i++) {
            petRepository.save(Pet.create("Pet " + i, "Cat", null,
                    null, null, null, null, "MC" + (10000 + i), null, null, clientId));
        }

        Page<Pet> page = petRepository.findAll(PageRequest.of(0, 3));

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void shouldUpdateExistingPet() {
        Pet saved = petRepository.save(testPet);

        saved.setWeight(BigDecimal.valueOf(35.0));
        saved.setMicrochipId("MC_UPDATED");
        petRepository.save(saved);

        Optional<Pet> found = petRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(BigDecimal.valueOf(35.0), found.get().getWeight());
        assertEquals("MC_UPDATED", found.get().getMicrochipId());
    }
}
