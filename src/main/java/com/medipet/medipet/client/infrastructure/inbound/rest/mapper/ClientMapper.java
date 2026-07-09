package com.medipet.medipet.client.infrastructure.inbound.rest.mapper;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.infrastructure.inbound.rest.dto.response.ClientResponse;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static ClientResponse toResponse(Client client) {
        return new ClientResponse(
                client.getId().getValue().toString(),
                client.getName(),
                client.getDni(),
                client.getPhone(),
                client.getEmail(),
                client.getAddress(),
                client.isActive()
        );
    }
}
