package com.medipet.medipet.user.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Schema(description = "User response")
public class UserResponse {

    @Schema(description = "User ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "User email", example = "vet@medipet.com")
    private String email;

    @Schema(description = "First name", example = "Maria")
    private String firstName;

    @Schema(description = "Last name", example = "Garcia")
    private String lastName;

    @Schema(description = "User role", example = "VETERINARIO")
    private String role;

    @Schema(description = "Whether the user is active", example = "true")
    private boolean active;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Schema(description = "Address", example = "Av. Corrientes 1234")
    private String address;

    @Schema(description = "Date of birth", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "Last login timestamp", example = "2025-07-05T18:30:00")
    private LocalDateTime lastLoginAt;
}
