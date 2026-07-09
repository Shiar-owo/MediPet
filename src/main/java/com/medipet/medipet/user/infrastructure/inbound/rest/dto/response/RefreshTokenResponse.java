package com.medipet.medipet.user.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Token refresh response")
public class RefreshTokenResponse {

    @Schema(description = "New JWT access token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String accessToken;

    @Schema(description = "New JWT refresh token", example = "eyJhbGciOiJIUzI1NiJ9...")
    private String refreshToken;
}
