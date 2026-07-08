package com.medipet.medipet.pet.application;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.inbound.CreatePetUseCase;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.shared.exception.ConflictException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
class CreatePetUseCaseImpl implements CreatePetUseCase {

    private final PetRepository petRepository;

    CreatePetUseCaseImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet execute(String name, String species, String breed, LocalDate dateOfBirth,
                       String sex, String color, BigDecimal weight, String microchipId,
                       String tattooNumber, String photoUrl, ClientId clientId) {
        if (microchipId != null && !microchipId.isBlank()) {
            if (petRepository.existsByMicrochipId(microchipId)) {
                throw new ConflictException("Pet with microchip " + microchipId + " already exists");
            }
        }

        if (tattooNumber != null && !tattooNumber.isBlank()) {
            if (petRepository.existsByTattooNumber(tattooNumber)) {
                throw new ConflictException("Pet with tattoo number " + tattooNumber + " already exists");
            }
        }

        Pet pet = Pet.create(name, species, breed, dateOfBirth, sex, color, weight,
                microchipId, tattooNumber, photoUrl, clientId);
        return petRepository.save(pet);
    }
}
