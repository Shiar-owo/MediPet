package com.medipet.medipet.client.infrastructure.outbound.persistence;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.client.infrastructure.outbound.persistence.mapper.ClientEntityMapper;
import com.medipet.medipet.client.infrastructure.outbound.persistence.repository.ClientJpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
class ClientPersistenceAdapter implements ClientRepository {

    private final ClientJpaRepository jpaRepository;

    ClientPersistenceAdapter(ClientJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Client save(Client client) {
        var entity = ClientEntityMapper.toEntity(client);
        var saved = jpaRepository.save(entity);
        return ClientEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Client> findById(ClientId id) {
        return jpaRepository.findById(id.getValue())
                .map(ClientEntityMapper::toDomain);
    }

    @Override
    public Optional<Client> findByDni(String dni) {
        return jpaRepository.findByDni(dni)
                .map(ClientEntityMapper::toDomain);
    }

    @Override
    public Page<Client> findByNameContaining(String name, Pageable pageable) {
        return jpaRepository.findByNameContainingIgnoreCase(name, pageable)
                .map(ClientEntityMapper::toDomain);
    }

    @Override
    public Page<Client> findAll(Pageable pageable) {
        return jpaRepository.findAll(pageable)
                .map(ClientEntityMapper::toDomain);
    }

    @Override
    public boolean existsByDni(String dni) {
        return jpaRepository.existsByDni(dni);
    }

    @Override
    public List<Client> findAllById(Collection<ClientId> ids) {
        return jpaRepository.findAllById(ids.stream().map(ClientId::getValue).toList())
                .stream()
                .map(ClientEntityMapper::toDomain)
                .toList();
    }
}
