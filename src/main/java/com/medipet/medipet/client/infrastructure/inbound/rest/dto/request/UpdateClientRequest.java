package com.medipet.medipet.client.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "Update client request (all fields optional)")
public class UpdateClientRequest {

    @Schema(description = "Client full name", example = "Maria Garcia")
    private String name;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Schema(description = "Email address", example = "maria@example.com")
    private String email;

    @Schema(description = "Physical address", example = "Av. Corrientes 1234")
    private String address;
}
