package com.medipet.medipet.client.infrastructure.outbound.persistence.mapper;

import com.medipet.medipet.client.infrastructure.outbound.persistence.entity.ClientEntity;
import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.valueobject.ClientId;

public class ClientEntityMapper {

    private ClientEntityMapper() {
    }

    public static ClientEntity toEntity(Client client) {
        ClientEntity entity = new ClientEntity();
        entity.setId(client.getId().getValue());
        entity.setName(client.getName());
        entity.setDni(client.getDni());
        entity.setPhone(client.getPhone());
        entity.setEmail(client.getEmail());
        entity.setAddress(client.getAddress());
        entity.setActive(client.isActive());
        return entity;
    }

    public static Client toDomain(ClientEntity entity) {
        return Client.withId(
                ClientId.from(entity.getId()),
                entity.getName(),
                entity.getDni(),
                entity.getPhone(),
                entity.getEmail(),
                entity.getAddress(),
                entity.isActive()
        );
    }
}
