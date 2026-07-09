package com.medipet.medipet.pet.application;

import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.inbound.UpdatePetUseCase;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.exception.ConflictException;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;

@Service
class UpdatePetUseCaseImpl implements UpdatePetUseCase {

    private final PetRepository petRepository;

    UpdatePetUseCaseImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Pet execute(PetId id, String name, String species, String breed, LocalDate dateOfBirth,
                       String sex, String color, BigDecimal weight, String microchipId,
                       String tattooNumber, String photoUrl) {
        Pet pet = petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet", id.getValue()));

        if (name != null) pet.setName(name);
        if (species != null) pet.setSpecies(species);
        if (breed != null) pet.setBreed(breed);
        if (dateOfBirth != null) pet.setDateOfBirth(dateOfBirth);
        if (sex != null) pet.setSex(sex);
        if (color != null) pet.setColor(color);
        if (weight != null) pet.setWeight(weight);
        if (tattooNumber != null) pet.setTattooNumber(tattooNumber);
        if (photoUrl != null) pet.setPhotoUrl(photoUrl);

        if (microchipId != null) {
            if (!microchipId.equals(pet.getMicrochipId()) && petRepository.existsByMicrochipId(microchipId)) {
                throw new ConflictException("Pet with microchip " + microchipId + " already exists");
            }
            pet.setMicrochipId(microchipId);
        }

        return petRepository.save(pet);
    }
}
