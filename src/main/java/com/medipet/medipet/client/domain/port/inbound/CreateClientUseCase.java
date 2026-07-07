package com.medipet.medipet.client.domain.port.inbound;

import com.medipet.medipet.client.domain.model.Client;

public interface CreateClientUseCase {

    Client execute(String name, String dni, String phone, String email, String address);
}
