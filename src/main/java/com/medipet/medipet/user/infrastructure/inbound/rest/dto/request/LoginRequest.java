package com.medipet.medipet.user.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Login request")
public class LoginRequest {

    @NotBlank
    @Email
    @Schema(description = "User email", example = "admin@medipet.com")
    private String email;

    @NotBlank
    @Schema(description = "User password", example = "admin123")
    private String password;
}
