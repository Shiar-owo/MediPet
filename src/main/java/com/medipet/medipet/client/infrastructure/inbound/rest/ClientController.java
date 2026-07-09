package com.medipet.medipet.client.infrastructure.inbound.rest;

import com.medipet.medipet.client.domain.port.inbound.ClientQueryService;
import com.medipet.medipet.client.domain.port.inbound.CreateClientUseCase;
import com.medipet.medipet.client.domain.port.inbound.UpdateClientUseCase;
import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.client.infrastructure.inbound.rest.dto.request.ClientRequest;
import com.medipet.medipet.client.infrastructure.inbound.rest.dto.request.UpdateClientRequest;
import com.medipet.medipet.client.infrastructure.inbound.rest.dto.response.ClientResponse;
import com.medipet.medipet.client.infrastructure.inbound.rest.mapper.ClientMapper;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Client management")
class ClientController {

    private final CreateClientUseCase createClientUseCase;
    private final UpdateClientUseCase updateClientUseCase;
    private final ClientQueryService clientQueryService;

    ClientController(CreateClientUseCase createClientUseCase,
                     UpdateClientUseCase updateClientUseCase,
                     ClientQueryService clientQueryService) {
        this.createClientUseCase = createClientUseCase;
        this.updateClientUseCase = updateClientUseCase;
        this.clientQueryService = clientQueryService;
    }

    @PostMapping
    @Operation(summary = "Create a new client", description = "Registers a new client with DNI, name, and optional contact info.")
    public ResponseEntity<ApiResponse<ClientResponse>> create(@Valid @RequestBody ClientRequest request) {
        var client = createClientUseCase.execute(
                request.getName(),
                request.getDni(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress());

        var response = ClientMapper.toResponse(client);
        URI location = URI.create("/api/clients/" + response.getId());
        return ResponseEntity.created(location).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a client", description = "Updates client fields. Only provided non-null fields are changed.")
    public ResponseEntity<ApiResponse<ClientResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdateClientRequest request) {
        var client = updateClientUseCase.execute(
                ClientId.from(id),
                request.getName(),
                request.getPhone(),
                request.getEmail(),
                request.getAddress());

        return ResponseEntity.ok(ApiResponse.ok(ClientMapper.toResponse(client)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get client by ID", description = "Returns a single client by their UUID.")
    public ResponseEntity<ApiResponse<ClientResponse>> getById(@PathVariable String id) {
        var client = clientQueryService.findById(ClientId.from(id));
        return ResponseEntity.ok(ApiResponse.ok(ClientMapper.toResponse(client)));
    }

    @GetMapping("/dni/{dni}")
    @Operation(summary = "Get client by DNI", description = "Returns a single client by their national ID number.")
    public ResponseEntity<ApiResponse<ClientResponse>> getByDni(@PathVariable String dni) {
        var client = clientQueryService.findByDni(dni);
        return ResponseEntity.ok(ApiResponse.ok(ClientMapper.toResponse(client)));
    }

    @GetMapping
    @Operation(summary = "List/search clients", description = "Returns a paginated list. Optional ?q= filters by name.")
    public ResponseEntity<ApiResponse<PageResponse<ClientResponse>>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var clients = clientQueryService.findAll(q, PageRequest.of(page, size));
        var response = new PageResponse<>(
                clients.getContent().stream().map(ClientMapper::toResponse).toList(),
                clients.getPage(),
                clients.getSize(),
                clients.getTotalElements(),
                clients.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
