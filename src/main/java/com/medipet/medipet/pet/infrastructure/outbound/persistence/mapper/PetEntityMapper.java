package com.medipet.medipet.pet.infrastructure.outbound.persistence.mapper;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.pet.infrastructure.outbound.persistence.entity.PetEntity;

public class PetEntityMapper {

    private PetEntityMapper() {
    }

    public static PetEntity toEntity(Pet pet) {
        PetEntity entity = new PetEntity();
        entity.setId(pet.getId().getValue());
        entity.setName(pet.getName());
        entity.setSpecies(pet.getSpecies());
        entity.setBreed(pet.getBreed());
        entity.setDateOfBirth(pet.getDateOfBirth());
        entity.setSex(pet.getSex());
        entity.setColor(pet.getColor());
        entity.setWeight(pet.getWeight());
        entity.setMicrochipId(pet.getMicrochipId());
        entity.setTattooNumber(pet.getTattooNumber());
        entity.setPhotoUrl(pet.getPhotoUrl());
        entity.setActive(pet.isActive());
        entity.setClientId(pet.getClientId().getValue());
        return entity;
    }

    public static Pet toDomain(PetEntity entity) {
        return Pet.withId(
                PetId.from(entity.getId()),
                entity.getName(),
                entity.getSpecies(),
                entity.getBreed(),
                entity.getDateOfBirth(),
                entity.getSex(),
                entity.getColor(),
                entity.getWeight(),
                entity.getMicrochipId(),
                entity.getTattooNumber(),
                entity.getPhotoUrl(),
                entity.isActive(),
                ClientId.from(entity.getClientId())
        );
    }
}
