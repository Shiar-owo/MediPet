package com.medipet.medipet.client.domain.port.inbound;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.valueobject.ClientId;

public interface UpdateClientUseCase {

    Client execute(ClientId id, String name, String phone, String email, String address);
}
