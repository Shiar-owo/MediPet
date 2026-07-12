package com.medipet.medipet.user.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Schema(description = "Register new user request (ADMIN only)")
public class RegisterUserRequest {

    @NotBlank
    @Email
    @Schema(description = "User email", example = "vet@medipet.com")
    private String email;

    @NotBlank
    @Schema(description = "User password", example = "password123")
    private String password;

    @Schema(description = "First name", example = "Maria")
    private String firstName;

    @Schema(description = "Last name", example = "Garcia")
    private String lastName;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Schema(description = "Address", example = "Av. Corrientes 1234")
    private String address;

    @Schema(description = "Date of birth (ISO-8601)", example = "1990-01-15")
    private LocalDate dateOfBirth;

    @Schema(description = "Avatar URL", example = "https://example.com/avatar.jpg")
    private String avatar;

    @Schema(description = "User role", example = "VETERINARIO", allowableValues = {"ADMIN", "VETERINARIO", "RECEPCIONISTA"})
    private String role;
}
