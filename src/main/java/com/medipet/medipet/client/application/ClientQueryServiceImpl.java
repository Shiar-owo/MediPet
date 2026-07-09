package com.medipet.medipet.client.application;

import com.medipet.medipet.client.domain.model.Client;
import com.medipet.medipet.client.domain.port.inbound.ClientQueryService;
import com.medipet.medipet.client.domain.port.outbound.ClientRepository;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.shared.dto.PageResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
class ClientQueryServiceImpl implements ClientQueryService {

    private final ClientRepository clientRepository;

    ClientQueryServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Client findById(ClientId id) {
        return clientRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Client", id.getValue()));
    }

    @Override
    @Transactional(readOnly = true)
    public Client findByDni(String dni) {
        return clientRepository.findByDni(dni)
                .orElseThrow(() -> new NotFoundException("Client not found with DNI: " + dni));
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<Client> findAll(String query, Pageable pageable) {
        Page<Client> page = (query != null && !query.isBlank())
                ? clientRepository.findByNameContaining(query, pageable)
                : clientRepository.findAll(pageable);
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages());
    }
}
