package com.medipet.medipet.pet.infrastructure.inbound.rest;

import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.pet.infrastructure.inbound.rest.dto.response.PetResponse;
import com.medipet.medipet.pet.infrastructure.inbound.rest.mapper.PetMapper;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.shared.port.outbound.FileStoragePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/pets")
@Tag(name = "Pets", description = "Pet management")
class PetPhotoController {

    private final PetRepository petRepository;
    private final FileStoragePort fileStoragePort;

    PetPhotoController(PetRepository petRepository, FileStoragePort fileStoragePort) {
        this.petRepository = petRepository;
        this.fileStoragePort = fileStoragePort;
    }

    @PostMapping("/{id}/photo")
    @Operation(summary = "Upload pet photo", description = "Uploads a photo for a pet. Accepted types: JPEG, PNG, GIF, WebP. Max size: 5MB.")
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPhoto(
            @PathVariable String id,
            @RequestParam("file") MultipartFile file) {
        var pet = petRepository.findById(PetId.from(id))
                .orElseThrow(() -> new NotFoundException("Pet", id));

        String photoUrl = fileStoragePort.upload(file, "medipet/pets");
        pet.setPhotoUrl(photoUrl);
        petRepository.save(pet);

        return ResponseEntity.ok(ApiResponse.ok(Map.of("photoUrl", photoUrl)));
    }
}
