package com.medipet.medipet.pet.application;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.inbound.PetQueryService;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
class PetQueryServiceImpl implements PetQueryService {

    private final PetRepository petRepository;

    PetQueryServiceImpl(PetRepository petRepository) {
        this.petRepository = petRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Pet findById(PetId id) {
        return petRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Pet", id.getValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> findByMicrochipId(String microchipId) {
        return petRepository.findByMicrochipId(microchipId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Pet> findByTattooNumber(String tattooNumber) {
        return petRepository.findByTattooNumber(tattooNumber);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Pet> findByClientId(ClientId clientId, Pageable pageable) {
        Page<Pet> page = petRepository.findByClientId(clientId, pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Pet> findAll(String query, Pageable pageable) {
        Page<Pet> page = (query != null && !query.isBlank())
                ? petRepository.findByNameContaining(query, pageable)
                : petRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
