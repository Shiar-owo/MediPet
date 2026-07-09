package com.medipet.medipet.pet.domain.port.inbound;

import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.valueobject.PetId;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface UpdatePetUseCase {

    Pet execute(PetId id, String name, String species, String breed, LocalDate dateOfBirth,
                String sex, String color, BigDecimal weight, String microchipId,
                String tattooNumber, String photoUrl);
}
