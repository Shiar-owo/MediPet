package com.medipet.medipet.user.domain.port.inbound;

public interface AuthService {

    LoginResult issueTokens(String email);

    LoginResult refreshToken(String refreshToken);

    void logout(String refreshToken);

    record LoginResult(String accessToken, String refreshToken, String role) {
    }
}
