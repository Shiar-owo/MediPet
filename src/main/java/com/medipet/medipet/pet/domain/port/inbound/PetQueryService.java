package com.medipet.medipet.pet.domain.port.inbound;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface PetQueryService {

    Pet findById(PetId id);

    Optional<Pet> findByMicrochipId(String microchipId);

    Optional<Pet> findByTattooNumber(String tattooNumber);

    PageResponse<Pet> findByClientId(ClientId clientId, Pageable pageable);

    PageResponse<Pet> findAll(String query, Pageable pageable);
}
