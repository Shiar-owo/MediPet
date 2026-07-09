package com.medipet.medipet.pet.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.infrastructure.inbound.rest.dto.response.PetResponse;

import java.time.format.DateTimeFormatter;

public final class PetMapper {

    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private PetMapper() {
    }

    public static PetResponse toResponse(Pet pet) {
        return new PetResponse(
                pet.getId().getValue().toString(),
                pet.getName(),
                pet.getSpecies(),
                pet.getBreed(),
                pet.getDateOfBirth() != null ? pet.getDateOfBirth().format(DATE_FORMAT) : null,
                pet.getSex(),
                pet.getColor(),
                pet.getWeight() != null ? pet.getWeight().toPlainString() : null,
                pet.getMicrochipId(),
                pet.getTattooNumber(),
                pet.getPhotoUrl(),
                pet.isActive(),
                pet.getClientId().getValue().toString()
        );
    }
}
