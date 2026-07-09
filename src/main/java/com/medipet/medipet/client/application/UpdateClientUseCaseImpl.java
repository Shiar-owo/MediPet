package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.inbound.UpdateClientUseCase;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class UpdateClientUseCaseImpl implements UpdateClientUseCase {

    private final ClientRepository clientRepository;

    UpdateClientUseCaseImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional
    public Client execute(ClientId id, String name, String phone, String email, String address) {
        Client client = clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client", id.getValue()));

        if (name != null) client.setName(name);
        if (phone != null) client.setPhone(phone);
        if (email != null) client.setEmail(email);
        if (address != null) client.setAddress(address);

        return clientRepository.save(client);
    }
}
