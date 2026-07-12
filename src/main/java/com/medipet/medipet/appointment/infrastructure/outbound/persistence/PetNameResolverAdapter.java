package com.medipet.medipet.appointment.infrastructure.outbound.persistence;

import com.medipet.medipet.appointment.domain.port.outbound.PetNameResolverPort;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class PetNameResolverAdapter implements PetNameResolverPort {

    private final PetRepository petRepository;

    PetNameResolverAdapter(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    public Map<PetId, String> resolveNames(Collection<PetId> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return petRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        com.medipet.medipet.pet.domain.model.Pet::getId,
                        com.medipet.medipet.pet.domain.model.Pet::getName,
                        (a, b) -> a));
    }
}
