package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.inbound.CreateClientUseCase;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.shared.exception.ConflictException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class CreateClientUseCaseImpl implements CreateClientUseCase {

    private final ClientRepository clientRepository;

    CreateClientUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Client execute(String name, String dni, String phone, String email, String address) {
        if (clientRepository.existsByDni(dni)) {
            throw new ConflictException("DNI already in use: " + dni);
        }

        Client client = Client.create(name, dni, phone, email, address);
        return clientRepository.save(client);
    }
}
