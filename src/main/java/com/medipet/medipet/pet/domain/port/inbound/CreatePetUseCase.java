package com.medipet.medipet.pet.domain.port.inbound;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CreatePetUseCase {

    Pet execute(String name, String species, String breed, LocalDate dateOfBirth,
                String sex, String color, BigDecimal weight, String microchipId,
                String tattooNumber, String photoUrl, ClientId clientId);
}
