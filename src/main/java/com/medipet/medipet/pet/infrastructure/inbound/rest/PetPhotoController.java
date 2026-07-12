package com.medipet.medipet.pet.infrastructure.inbound.rest;

import com.medipet.medipet.pet.domain.port.outbound.PetRepository;
import com.medipet.medipet.pet.domain.valueobject.PetId;
import com.medipet.medipet.shared.dto.ApiResponse;
import com.medipet.medipet.shared.exception.NotFoundException;
import com.medipet.medipet.shared.port.outbound.FileStoragePort;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.MediaType;
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

    @PostMapping(value = "/{id}/photo", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload pet photo",
            description = "Uploads a photo for a pet. Accepted types: JPEG, PNG, GIF, WebP. Max size: 5MB. The photo is stored in Cloudinary and the URL is saved to the pet's photoUrl field.")
    @io.swagger.v3.oas.annotations.responses.ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Photo uploaded successfully",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = PhotoUploadResponse.class))),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Invalid file type or size exceeds 5MB",
                    content = @Content(mediaType = "application/json")),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Pet not found",
                    content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<ApiResponse<Map<String, String>>> uploadPhoto(
            @Parameter(description = "Pet UUID", required = true, example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
            @PathVariable String id,
            @Parameter(description = "Photo file (JPEG, PNG, GIF, WebP. Max 5MB)", required = true)
            @RequestParam("file") MultipartFile file) {
        var pet = petRepository.findById(PetId.from(id))
                .orElseThrow(() -> new NotFoundException("Pet", id));

        String photoUrl = fileStoragePort.upload(file, "medipet/pets");
        pet.setPhotoUrl(photoUrl);
        petRepository.save(pet);

        return ResponseEntity.ok(ApiResponse.ok(Map.of("photoUrl", photoUrl)));
    }

    @Schema(description = "Photo upload response")
    static class PhotoUploadResponse {
        @Schema(description = "Cloudinary URL of the uploaded photo", example = "https://res.cloudinary.com/demo/image/upload/v1/medipet/pets/photo.jpg")
        public String photoUrl;
    }
}
