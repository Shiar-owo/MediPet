package com.medipet.medipet.pet.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Pet response")
public class PetResponse {

    @Schema(description = "Pet ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Pet name", example = "Max")
    private String name;

    @Schema(description = "Species", example = "Dog")
    private String species;

    @Schema(description = "Breed", example = "Golden Retriever")
    private String breed;

    @Schema(description = "Date of birth", example = "2020-05-15")
    private String dateOfBirth;

    @Schema(description = "Sex", example = "MALE")
    private String sex;

    @Schema(description = "Color", example = "Golden")
    private String color;

    @Schema(description = "Weight in kg", example = "30.5")
    private String weight;

    @Schema(description = "Microchip ID", example = "MC12345")
    private String microchipId;

    @Schema(description = "Tattoo number", example = "TAT001")
    private String tattooNumber;

    @Schema(description = "Photo URL", example = "https://example.com/max.jpg")
    private String photoUrl;

    @Schema(description = "Whether the pet is active", example = "true")
    private boolean active;

    @Schema(description = "Owner client ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String clientId;
}
