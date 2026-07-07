package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientQueryServiceImplTest {

    @Mock
    private ClientRepository clientRepository;

    private ClientQueryServiceImpl clientQueryService;

    private ClientId clientId;
    private Client testClient;

    @BeforeEach
    void setUp() {
        clientQueryService = new ClientQueryServiceImpl(clientRepository);
        clientId = ClientId.from(UUID.randomUUID());
        testClient = Client.withId(clientId, "Maria Garcia", "12345678",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234", true);
    }

    @Test
    void shouldFindById() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(testClient));

        Client result = clientQueryService.findById(clientId);

        assertEquals("Maria Garcia", result.getName());
        assertEquals("12345678", result.getDni());
    }

    @Test
    void shouldThrowWhenNotFoundById() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientQueryService.findById(clientId));
    }

    @Test
    void shouldFindByDni() {
        when(clientRepository.findByDni("12345678")).thenReturn(Optional.of(testClient));

        Client result = clientQueryService.findByDni("12345678");

        assertEquals("Maria Garcia", result.getName());
    }

    @Test
    void shouldThrowWhenNotFoundByDni() {
        when(clientRepository.findByDni("00000000")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> clientQueryService.findByDni("00000000"));
    }

    @Test
    void shouldSearchByNameWhenQueryProvided() {
        Page<Client> page = new PageImpl<>(List.of(testClient), PageRequest.of(0, 10), 1);
        when(clientRepository.findByNameContaining("Maria", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Client> result = clientQueryService.findAll("Maria", PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals("Maria Garcia", result.getContent().get(0).getName());
        verify(clientRepository).findByNameContaining("Maria", PageRequest.of(0, 10));
        verify(clientRepository, never()).findAll(any());
    }

    @Test
    void shouldReturnAllWhenQueryIsNull() {
        Page<Client> page = new PageImpl<>(List.of(testClient), PageRequest.of(0, 20), 1);
        when(clientRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Client> result = clientQueryService.findAll(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        verify(clientRepository).findAll(PageRequest.of(0, 20));
        verify(clientRepository, never()).findByNameContaining(any(), any());
    }

    @Test
    void shouldReturnAllWhenQueryIsBlank() {
        Page<Client> page = new PageImpl<>(List.of(testClient), PageRequest.of(0, 20), 1);
        when(clientRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Client> result = clientQueryService.findAll("   ", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(clientRepository).findAll(PageRequest.of(0, 20));
        verify(clientRepository, never()).findByNameContaining(any(), any());
    }
}
