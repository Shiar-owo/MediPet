package com.medipet.medipet.client.domain.port.inbound;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.dto.PageResponse;
import org.springframework.data.domain.Pageable;

public interface ClientQueryService {

    Client findById(ClientId id);

    Client findByDni(String dni);

    PageResponse<Client> findAll(String query, Pageable pageable);
}
