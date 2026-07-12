package com.medipet.medipet.user.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Login response with JWT tokens")
public class LoginResponse {

    @Schema(description = "User ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String userId;

    @Schema(description = "JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;

    @Schema(description = "Authenticated user email", example = "admin@medipet.com")
    private String email;

    @Schema(description = "User role", example = "ADMIN")
    private String role;
}
