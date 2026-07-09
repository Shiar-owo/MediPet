package com.medipet.medipet.user.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Update user request (all fields optional)")
public class UpdateUserRequest {

    @Schema(description = "First name", example = "Maria")
    private String firstName;

    @Schema(description = "Last name", example = "Garcia")
    private String lastName;

    @Schema(description = "User role", example = "VETERINARIO", allowableValues = {"ADMIN", "VETERINARIO", "RECEPCIONISTA"})
    private String role;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Schema(description = "Address", example = "Av. Corrientes 1234")
    private String address;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatar;
}
