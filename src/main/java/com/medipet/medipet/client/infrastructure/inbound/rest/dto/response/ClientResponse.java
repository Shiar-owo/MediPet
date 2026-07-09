package com.medipet.medipet.client.infrastructure.inbound.rest.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "Client response")
public class ClientResponse {

    @Schema(description = "Client ID", example = "a1b2c3d4-e5f6-7890-abcd-ef1234567890")
    private String id;

    @Schema(description = "Client full name", example = "Maria Garcia")
    private String name;

    @Schema(description = "National ID number", example = "12345678")
    private String dni;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Schema(description = "Email address", example = "maria@example.com")
    private String email;

    @Schema(description = "Physical address", example = "Av. Corrientes 1234")
    private String address;

    @Schema(description = "Whether the client is active", example = "true")
    private boolean active;
}
