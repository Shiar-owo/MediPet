package com.medipet.medipet.pet.infrastructure.outbound.persistence;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.model.Pet;
import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.pet.infrastructure.outbound.persistence.mapper.PetEntityMapper;
import com.medipet.medipet.pet.infrastructure.outbound.persistence.repository.PetJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
class PetPersistenceAdapter implements PetRepository {

    private final PetJpaRepository jpaRepository;

    PetPersistenceAdapter(PetJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Pet save(Pet pet) {
        var entity = PetEntityMapper.toEntity(pet);
        var saved = jpaRepository.save(entity);
        return PetEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Pet> findById(PetId id) {
        return jpaRepository.findById(id.getValue())
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public Optional<Pet> findByMicrochipId(String microchipId) {
        return jpaRepository.findByMicrochipId(microchipId)
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public Optional<Pet> findByTattooNumber(String tattooNumber) {
        return jpaRepository.findByTattooNumber(tattooNumber)
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public Page<Pet> findByClientId(ClientId clientId, Pageable pageable) {
        return jpaRepository.findByClientId(clientId.getValue(), pageable)
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public Page<Pet> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public Page<Pet> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(PetEntityMapper::toDomain);
    }

    @Override
    public boolean existsByMicrochipId(String microchipId) {
        return jpaRepository.existsByMicrochipId(microchipId);
    }

    @Override
    public boolean existsByTattooNumber(String tattooNumber) {
        return jpaRepository.existsByTattooNumber(tattooNumber);
    }

    @Override
    public List<Pet> findAllById(Collection<PetId> ids) {
        return jpaRepository.findAllById(ids.stream().map(PetId::getValue).toList())
                .stream()
                .map(PetEntityMapper::toDomain)
                .toList();
    }
}
