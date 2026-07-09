package com.medipet.medipet.pet.application;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PetQueryServiceImplTest {

    @Mock
    private PetRepository petRepository;

    private PetQueryServiceImpl petQueryService;

    private PetId petId;
    private ClientId clientId;
    private Pet testPet;

    @BeforeEach
    void setUp() {
        petQueryService = new PetQueryServiceImpl(petRepository);
        petId = PetId.from(UUID.randomUUID());
        clientId = ClientId.from(UUID.randomUUID());
        testPet = Pet.withId(petId, "Max", "Dog", "Golden Retriever",
                LocalDate.of(2020, 5, 15), "MALE", "Golden", BigDecimal.valueOf(30.5),
                "MC12345", "TAT001", null, true, clientId);
    }

    @Test
    void shouldFindById() {
        when(petRepository.findById(petId)).thenReturn(Optional.of(testPet));

        Pet result = petQueryService.findById(petId);

        assertEquals("Max", result.getName());
        assertEquals("MC12345", result.getMicrochipId());
    }

    @Test
    void shouldThrowWhenNotFoundById() {
        when(petRepository.findById(petId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> petQueryService.findById(petId));
    }

    @Test
    void shouldFindByMicrochipId() {
        when(petRepository.findByMicrochipId("MC12345")).thenReturn(Optional.of(testPet));

        Optional<Pet> result = petQueryService.findByMicrochipId("MC12345");

        assertTrue(result.isPresent());
        assertEquals("Max", result.get().getName());
    }

    @Test
    void shouldFindByTattooNumber() {
        when(petRepository.findByTattooNumber("TAT001")).thenReturn(Optional.of(testPet));

        Optional<Pet> result = petQueryService.findByTattooNumber("TAT001");

        assertTrue(result.isPresent());
        assertEquals("Max", result.get().getName());
    }

    @Test
    void shouldFindByClientId() {
        Page<Pet> page = new PageImpl<>(List.of(testPet), PageRequest.of(0, 10), 1);
        when(petRepository.findByClientId(clientId, PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Pet> result = petQueryService.findByClientId(clientId, PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        assertEquals(1, result.getTotalElements());
        assertEquals("Max", result.getContent().get(0).getName());
    }

    @Test
    void shouldSearchByNameWhenQueryProvided() {
        Page<Pet> page = new PageImpl<>(List.of(testPet), PageRequest.of(0, 10), 1);
        when(petRepository.findByNameContaining("Max", PageRequest.of(0, 10))).thenReturn(page);

        PageResponse<Pet> result = petQueryService.findAll("Max", PageRequest.of(0, 10));

        assertEquals(1, result.getContent().size());
        verify(petRepository).findByNameContaining("Max", PageRequest.of(0, 10));
        verify(petRepository, never()).findAll(any());
    }

    @Test
    void shouldReturnAllWhenQueryIsNull() {
        Page<Pet> page = new PageImpl<>(List.of(testPet), PageRequest.of(0, 20), 1);
        when(petRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Pet> result = petQueryService.findAll(null, PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(petRepository).findAll(PageRequest.of(0, 20));
        verify(petRepository, never()).findByNameContaining(any(), any());
    }

    @Test
    void shouldReturnAllWhenQueryIsBlank() {
        Page<Pet> page = new PageImpl<>(List.of(testPet), PageRequest.of(0, 20), 1);
        when(petRepository.findAll(PageRequest.of(0, 20))).thenReturn(page);

        PageResponse<Pet> result = petQueryService.findAll("   ", PageRequest.of(0, 20));

        assertEquals(1, result.getContent().size());
        verify(petRepository).findAll(PageRequest.of(0, 20));
    }
}
