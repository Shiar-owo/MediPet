package com.medipet.medipet.pet.application;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.shared.exception.ConflictException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreatePetUseCaseImplTest {

    @Mock
    private PetRepository petRepository;

    private CreatePetUseCaseImpl createPetUseCase;

    private ClientId clientId;

    @BeforeEach
    void setUp() {
        createPetUseCase = new CreatePetUseCaseImpl(petRepository);
        clientId = ClientId.from(UUID.randomUUID());
    }

    @Test
    void shouldCreatePetSuccessfully() {
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet result = createPetUseCase.execute("Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, clientId);

        assertNotNull(result);
        assertEquals("Max", result.getName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("Golden Retriever", result.getBreed());
        assertEquals("MC12345", result.getMicrochipId());
        assertEquals("TAT001", result.getTattooNumber());
        assertTrue(result.isActive());
        assertEquals(clientId, result.getClientId());
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldThrowConflictWhenMicrochipExists() {
        when(petRepository.existsByMicrochipId("MC12345")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                createPetUseCase.execute("Max", "Dog", "Golden Retriever",
                        LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                        "MC12345", null, null, clientId));

        verify(petRepository).existsByMicrochipId("MC12345");
        verify(petRepository, never()).save(any());
    }

    @Test
    void shouldThrowConflictWhenTattooExists() {
        when(petRepository.existsByTattooNumber("TAT001")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                createPetUseCase.execute("Max", "Dog", null, null, null, null, null,
                        null, "TAT001", null, clientId));

        verify(petRepository).existsByTattooNumber("TAT001");
        verify(petRepository, never()).save(any());
    }
}
