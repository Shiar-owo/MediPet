package com.medipet.medipet.appointment.infrastructure.outbound.persistence;

import com.medipet.medipet.appointment.domain.port.outbound.ClientNameResolverPort;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Map;
import java.util.stream.Collectors;

@Component
class ClientNameResolverAdapter implements ClientNameResolverPort {

    private final ClientRepository clientRepository;

    ClientNameResolverAdapter(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public Map<ClientId, String> resolveNames(Collection<ClientId> ids) {
        if (ids.isEmpty()) {
            return Map.of();
        }
        return clientRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(
                        com.medipet.medipet.client.domain.model.Client::getId,
                        com.medipet.medipet.client.domain.model.Client::getName,
                        (a, b) -> a));
    }
}
