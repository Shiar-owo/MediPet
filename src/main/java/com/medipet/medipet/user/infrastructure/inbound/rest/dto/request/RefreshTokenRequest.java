package com.medipet.medipet.user.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Refresh token request")
public class RefreshTokenRequest {

    @NotBlank
    @Schema(description = "Refresh token to exchange", example = "eyJhbGciOi...")
    private String refreshToken;
}
