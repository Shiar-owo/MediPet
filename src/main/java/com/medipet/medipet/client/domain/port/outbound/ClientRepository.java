package com.medipet.medipet.client.domain.port.outbound;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface ClientRepository {

    Client save(Client client);

    Optional<Client> findById(ClientId id);

    Optional<Client> findByDni(String dni);

    Page<Client> findByNameContaining(String name, Pageable pageable);

    Page<Client> findAll(Pageable pageable);

    boolean existsByDni(String dni);
}
