package com.medipet.medipet.client.infrastructure.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
@Schema(description = "Client creation request")
public class ClientRequest {

    @NotBlank
    @Schema(description = "Client full name", example = "Maria Garcia")
    private String name;

    @NotBlank
    @Schema(description = "National ID number", example = "12345678")
    private String dni;

    @Schema(description = "Phone number", example = "+5491155551234")
    private String phone;

    @Email
    @Schema(description = "Email address", example = "maria@example.com")
    private String email;

    @Schema(description = "Physical address", example = "Av. Corrientes 1234")
    private String address;
}
