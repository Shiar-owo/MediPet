package com.medipet.medipet.pet.application;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdatePetUseCaseImplTest {

    @Mock
    private PetRepository petRepository;

    private UpdatePetUseCaseImpl updatePetUseCase;

    private PetId petId;
    private ClientId clientId;
    private Pet existingPet;

    @BeforeEach
    void setUp() {
        updatePetUseCase = new UpdatePetUseCaseImpl(petRepository);
        petId = PetId.from(UUID.randomUUID());
        clientId = ClientId.from(UUID.randomUUID());
        existingPet = Pet.withId(petId, "Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, true, clientId);
    }

    @Test
    void shouldUpdatePetSuccessfully() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet result = updatePetUseCase.execute(petId, "Maxi", "Dog", "Labrador",
                LocalDate.of(2021, 1, 1), "MALE", "Black", BigDecimal.valueOf(25.0),
                null, null, null);

        assertEquals("Maxi", result.getName());
        assertEquals("Labrador", result.getBreed());
        assertEquals("Black", result.getColor());
        assertEquals("MC12345", result.getMicrochipId());
        verify(petRepository).save(any(Pet.class));
    }

    @Test
    void shouldThrowWhenPetNotFound() {
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () ->
                updatePetUseCase.execute(petId, "Maxi", null, null, null, null, null,
                        null, null, null, null));
    }

    @Test
    void shouldThrowConflictWhenMicrochipChangedAndExists() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        when(petRepository.existsByMicrochipId("MC99999")).thenReturn(true);

        assertThrows(ConflictException.class, () ->
                updatePetUseCase.execute(petId, null, null, null, null, null, null,
                        null, "MC99999", null, null));

        verify(petRepository).existsByMicrochipId("MC99999");
        verify(petRepository, never()).save(any());
    }

    @Test
    void shouldNotCheckMicrochipWhenSameAsExisting() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet result = updatePetUseCase.execute(petId, null, null, null, null, null, null,
                null, "MC12345", null, null);

        assertEquals("MC12345", result.getMicrochipId());
        verify(petRepository, never()).existsByMicrochipId(any());
    }

    @Test
    void shouldNotChangeFieldsWhenNull() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(existingPet));
        when(petRepository.save(any(Pet.class))).thenAnswer(inv -> inv.getArgument(0));

        Pet result = updatePetUseCase.execute(petId, null, null, null, null, null, null,
                null, null, null, null);

        assertEquals("Max", result.getName());
        assertEquals("Dog", result.getSpecies());
        assertEquals("Golden Retriever", result.getBreed());
        assertEquals(BigDecimal.valueOf(30.5), result.getWeight());
    }
}
