package com.medipet.medipet.user.infrastructure.inbound.rest.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class LoginResponse {

    private String accessToken;
    private String refreshToken;
    private String email;
    private String role;
}
