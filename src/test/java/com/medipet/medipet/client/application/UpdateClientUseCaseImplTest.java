package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateClientUseCaseImplTest {

    @Mock
    private ClientRepository clientRepository;

    private UpdateClientUseCaseImpl updateClientUseCase;

    private ClientId clientId;
    private Client existingClient;

    @BeforeEach
    void setUp() {
        updateClientUseCase = new UpdateClientUseCaseImpl(clientRepository);
        clientId = ClientId.from(UUID.randomUUID());
        existingClient = Client.withId(clientId, "Maria Garcia", "12345678",
                "+549115551234", "maria@example.com", "Av. Corrientes 1234", true);
    }

    @Test
    void shouldUpdateClientSuccessfully() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = updateClientUseCase.execute(clientId, "Maria Lopez",
                "+549115559999", null, null);

        assertEquals("Maria Lopez", result.getName());
        assertEquals("+549115559999", result.getPhone());
        assertEquals("maria@example.com", result.getEmail());
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowWhenClientNotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                updateClientUseCase.execute(clientId, "Maria Lopez", null, null, null));
    }

    @Test
    void shouldNotChangeFieldsWhenNull() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(existingClient));
        when(clientRepository.save(any(Client.class))).thenAnswer(inv -> inv.getArgument(0));

        Client result = updateClientUseCase.execute(clientId, null, null, null, null);

        assertEquals("Maria Garcia", result.getName());
        assertEquals("12345678", result.getDni());
        assertEquals("+549115551234", result.getPhone());
        assertEquals("maria@example.com", result.getEmail());
    }
}
