package com.medipet.medipet.pet.domain.port.outbound;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface PetRepository {

    Pet save(Pet pet);

    Optional<Pet> findById(PetId id);

    Optional<Pet> findByMicrochipId(String microchipId);

    Optional<Pet> findByTattooNumber(String tattooNumber);

    Page<Pet> findByClientId(ClientId clientId, Pageable pageable);

    Page<Pet> findByNameContaining(String name, Pageable pageable);

    Page<Pet> findAll(Pageable pageable);

    boolean existsByMicrochipId(String microchipId);

    boolean existsByTattooNumber(String tattooNumber);

    List<Pet> findAllById(Collection<PetId> ids);
}
