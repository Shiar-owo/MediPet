package com.medipet.medipet.pet.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
@Schema(description = "Pet creation request")
public class PetRequest {

    @NotBlank
    @Schema(description = "Pet name", example = "Max")
    private String name;

    @NotBlank
    @Schema(description = "Owner client ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String clientId;

    @NotBlank
    @Schema(description = "Species", example = "Dog")
    private String species;

    @Schema(description = "Breed", example = "Golden Retriever")
    private String breed;

    @Schema(description = "Date of birth (ISO-8601)", example = "2020-05-15")
    private String dateOfBirth;

    @Schema(description = "Sex", example = "MALE", allowableValues = {"MALE", "FEMALE"})
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
}
