package com.medipet.medipet.client.infrastructure.outbound.persistence;

import com.medipet.medipet.TestcontainersConfiguration;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@ActiveProfiles("test")
@Transactional
class ClientPersistenceAdapterTest {

    @Autowired
    private ClientRepository clientRepository;

    private Client testClient;

    @BeforeEach
    void setUp() {
        testClient = Client.create("Maria Garcia", "12345678",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234");
    }

    @Test
    void shouldSaveAndFindById() {
        Client saved = clientRepository.save(testClient);

        Optional<Client> found = clientRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Maria Garcia", found.get().getName());
        assertEquals("12345678", found.get().getDni());
        assertTrue(found.get().isActive());
    }

    @Test
    void shouldFindByDni() {
        clientRepository.save(testClient);

        Optional<Client> found = clientRepository.findByDni("12345678");

        assertTrue(found.isPresent());
        assertEquals("Maria Garcia", found.get().getName());
    }

    @Test
    void shouldReturnEmptyWhenDniNotFound() {
        Optional<Client> found = clientRepository.findByDni("00000000");

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnTrueWhenDniExists() {
        clientRepository.save(testClient);

        assertTrue(clientRepository.existsByDni("12345678"));
    }

    @Test
    void shouldReturnFalseWhenDniDoesNotExist() {
        assertFalse(clientRepository.existsByDni("00000000"));
    }

    @Test
    void shouldFindAllPaginated() {
        for (int i = 0; i < 5; i++) {
            clientRepository.save(Client.create("Client " + i, String.valueOf(10000000 + i),
                    null, null, null));
        }

        Page<Client> page = clientRepository.findAll(PageRequest.of(0, 3));

        assertEquals(3, page.getContent().size());
        assertEquals(5, page.getTotalElements());
        assertEquals(2, page.getTotalPages());
    }

    @Test
    void shouldUpdateExistingClient() {
        Client saved = clientRepository.save(testClient);

        saved.setPhone("+549115559999");
        clientRepository.save(saved);

        Optional<Client> found = clientRepository.findById(saved.getId());
        assertTrue(found.isPresent());
        assertEquals("+549115559999", found.get().getPhone());
    }
}
