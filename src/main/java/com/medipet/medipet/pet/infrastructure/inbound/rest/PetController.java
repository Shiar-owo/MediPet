package com.medipet.medipet.pet.infrastructure.inbound.rest;

import com.medipet.medipet.client.domain.valueobject.ClientId;
import com.medipet.medipet.pet.domain.port.inbound.CreatePetUseCase;
import com.medipet.medipet.pet.domain.port.inbound.PetQueryService;
import com.medipet.medipet.pet.domain.port.inbound.UpdatePetUseCase;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.pet.infrastructure.inbound.rest.dto.request.PetRequest;
import com.medipet.medipet.pet.infrastructure.inbound.rest.dto.request.UpdatePetRequest;
import com.medipet.medipet.pet.infrastructure.inbound.rest.dto.response.PetResponse;
import com.medipet.medipet.pet.infrastructure.inbound.rest.mapper.PetMapper;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.dto.PageResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet management")
class PetController {

    private final CreatePetUseCase createPetUseCase;
    private final UpdatePetUseCase updatePetUseCase;
    private final PetQueryService petQueryService;

    PetController(CreatePetUseCase createPetUseCase,
                  UpdatePetUseCase updatePetUseCase,
                  PetQueryService petQueryService) {
        this.createPetUseCase = createPetUseCase;
        this.updatePetUseCase = updatePetUseCase;
        this.petQueryService = petQueryService;
    }

    @PostMapping
    @Operation(summary = "Register a new pet", description = "Registers a new pet for an existing client.")
    public ResponseEntity<ApiResponse<PetResponse>> create(@Valid @RequestBody PetRequest request) {
        var pet = createPetUseCase.execute(
                request.getName(),
                request.getSpecies(),
                request.getBreed(),
                parseDate(request.getDateOfBirth()),
                request.getSex(),
                request.getColor(),
                parseBigDecimal(request.getWeight()),
                request.getMicrochipId(),
                request.getTattooNumber(),
                request.getPhotoUrl(),
                ClientId.from(request.getClientId()));

        var response = PetMapper.toResponse(pet);
        URI location = URI.create("/api/pets/" + response.getId());
        return ResponseEntity.created(location).body(ApiResponse.ok(response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a pet", description = "Updates pet fields. Only provided non-null fields are changed.")
    public ResponseEntity<ApiResponse<PetResponse>> update(
            @PathVariable String id,
            @Valid @RequestBody UpdatePetRequest request) {
        var pet = updatePetUseCase.execute(
                PetId.from(id),
                request.getName(),
                request.getSpecies(),
                request.getBreed(),
                parseDate(request.getDateOfBirth()),
                request.getSex(),
                request.getColor(),
                parseBigDecimal(request.getWeight()),
                request.getMicrochipId(),
                request.getTattooNumber(),
                request.getPhotoUrl());

        return ResponseEntity.ok(ApiResponse.ok(PetMapper.toResponse(pet)));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get pet by ID", description = "Returns a single pet by their UUID.")
    public ResponseEntity<ApiResponse<PetResponse>> getById(@PathVariable String id) {
        var pet = petQueryService.findById(PetId.from(id));
        return ResponseEntity.ok(ApiResponse.ok(PetMapper.toResponse(pet)));
    }

    @GetMapping("/microchip/{microchipId}")
    @Operation(summary = "Get pet by microchip", description = "Returns a single pet by their microchip ID.")
    public ResponseEntity<ApiResponse<PetResponse>> getByMicrochip(@PathVariable String microchipId) {
        return petQueryService.findByMicrochipId(microchipId)
                .map(pet -> ResponseEntity.ok(ApiResponse.ok(PetMapper.toResponse(pet))))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/client/{clientId}")
    @Operation(summary = "List pets by client", description = "Returns paginated pets belonging to a specific client.")
    public ResponseEntity<ApiResponse<PageResponse<PetResponse>>> getByClientId(
            @PathVariable String clientId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pets = petQueryService.findByClientId(ClientId.from(clientId), PageRequest.of(page, size));
        var response = new PageResponse<>(
                pets.getContent().stream().map(PetMapper::toResponse).toList(),
                pets.getPage(),
                pets.getSize(),
                pets.getTotalElements(),
                pets.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @GetMapping
    @Operation(summary = "List/search pets", description = "Returns a paginated list. Optional ?q= filters by name.")
    public ResponseEntity<ApiResponse<PageResponse<PetResponse>>> getAll(
            @RequestParam(required = false) String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        var pets = petQueryService.findAll(q, PageRequest.of(page, size));
        var response = new PageResponse<>(
                pets.getContent().stream().map(PetMapper::toResponse).toList(),
                pets.getPage(),
                pets.getSize(),
                pets.getTotalElements(),
                pets.getTotalPages());
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) return null;
        return LocalDate.parse(date);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null || value.isBlank()) return null;
        return new BigDecimal(value);
    }
}
