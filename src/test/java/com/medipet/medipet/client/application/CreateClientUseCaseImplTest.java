package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateClientUseCaseImplTest {

    @Mock
    private ClientRepository clientRepository;

    private CreateClientUseCaseImpl createClientUseCase;

    @BeforeEach
    void setUp() {
        createClientUseCase = new CreateClientUseCaseImpl(clientRepository);
    }

    @Test
    void shouldCreateClientSuccessfully() {
        when(clientRepository.existsByDni("12345678")).thenReturn(false);
        when(clientRepository.save(any(Client.class))).thenAnswer(invocation -> {
            Client c = invocation.getArgument(0);
            return Client.withId(c.getId(), c.getName(), c.getDni(), c.getPhone(),
                    c.getEmail(), c.getAddress(), c.isActive());
        });

        Client result = createClientUseCase.execute("Maria Garcia", "12345678",
                "+5491155551234", "maria@example.com", "Av. Corrientes 1234");

        assertNotNull(result);
        assertEquals("Maria Garcia", result.getName());
        assertEquals("12345678", result.getDni());
        assertTrue(result.isActive());
        verify(clientRepository).existsByDni("12345678");
        verify(clientRepository).save(any(Client.class));
    }

    @Test
    void shouldThrowConflictWhenDniExists() {
        when(clientRepository.existsByDni("12345678")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                createClientUseCase.execute("Maria Garcia", "12345678",
                        "+549115551234", "maria@example.com", "Av. Corrientes 1234"));

        verify(clientRepository).existsByDni("12345678");
        verify(clientRepository, never()).save(any());
    }
}
